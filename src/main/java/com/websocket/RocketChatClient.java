package com.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.websocket.request.*;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RocketChatClient implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(RocketChatClient.class.getName());

    private final Gson gson = new Gson();

    private final String url;
    Session session;
    private final CompletableFuture<String> connectResult = new CompletableFuture<>();
    private static final Map<String, CompletableFutureWithMapper<?>> futureResults = new HashMap<>();
    private static final Map<String, ObservableWithMapper<?>> subscriptionResults = new ConcurrentHashMap<>();


    public RocketChatClient(String url) {
        this.url = url;
    }

    public CompletableFuture<String> connect() {
        if (!connectResult.isDone()) { // TODO check if connection in progress
            try {
                LOG.info("connecting to " + url);
                WSClient clientEndpoint = new WSClient();
                session = ContainerProvider.getWebSocketContainer().connectToServer(clientEndpoint, URI.create(url));
                LOG.info("created session: " + session);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return connectResult;
    }

    public CompletableFuture<String> login(String user, String password) {
        return send(new MethodRequest("login", new LoginParam(user, password)),
                r -> (String) r.resultAsMap().get("token"));
    }

    public CompletableFuture<List<Subscription>> getSubscriptions() {
        return send(new MethodRequest("subscriptions/get"),
                genericAnswer -> {
                    JsonElement jsonElement = gson.toJsonTree(genericAnswer.resultAsList());
                    return gson.fromJson(jsonElement, new TypeToken<List<Subscription>>() {
                    }.getType());
                });
    }

    protected <T> CompletableFutureWithMapper<T> send(IRequest request, Function<GenericAnswer, T> answerMapper) {
        String id = request.getId();
        CompletableFutureWithMapper<T> result = new CompletableFutureWithMapper<>(answerMapper);
        futureResults.put(id, result);
        String requestString = gson.toJson(request);
        LOG.info("REQUEST: " + requestString);
        session.getAsyncRemote().sendText(requestString,
                sendResult -> handleSendResult(sendResult, result));
        return result;
    }

    public CompletableFuture<GenericAnswer> sendMessage(String msg, String rid) {
        return send(new MethodRequest("sendMessage", new SendMessageParam(msg, rid)), r -> r);
    }

    // TODO refactor with further stream methods
    public Observable<ChatMessage> streamRoomMessages(String rid) {
        return Observable.defer(() -> {
            if (subscriptionResults.containsKey(rid)) {
                return (Observable<ChatMessage>) subscriptionResults.get(rid).observable;
            }
            PublishSubject<ChatMessage> observableSource = PublishSubject.create();

            subscriptionResults.put(rid, new ObservableWithMapper<>(observableSource,
                    r -> gson.fromJson(gson.toJsonTree(((List<?>) r.fields.get("args")).get(0)), ChatMessage.class)));

            SubscriptionRequest request = new SubscriptionRequest("stream-room-messages", rid, false);
            send(request, Function.identity()).handleAsync((r, error) -> {
                if (error != null) {
                    observableSource.onError(error);
                }
                return CompletableFuture.completedFuture(error);
            });
            return observableSource.doFinally(() -> {
                LOG.info("cancelling subscription");
                send(new UnsubscribeRequest(request.getId()), Function.identity()).handleAsync((r, error) -> {
                    if (error != null) {
                        LOG.log(Level.WARNING, "Failed to unsubscribe: ", error);
                    }
                    subscriptionResults.remove(rid);
                    return CompletableFuture.completedFuture(r);
                });
            }).onTerminateDetach();
        }).onTerminateDetach();
    }

    private static void handleSendResult(SendResult sendResult, CompletableFuture<?> result) {
        if (!sendResult.isOK()) {
            result.completeExceptionally(new SendFailedException(sendResult));
        }
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Could not close session: ", e);
            }
        }
    }

    @ClientEndpoint
    public class WSClient {

        public WSClient() {
            LOG.info("created WSClient");
        }

        @OnMessage
        public void onMessage(String message) {
            LOG.info("Received msg: " + message);
            GenericAnswer msgObject = gson.fromJson(message, GenericAnswer.class);
            LOG.info("msg object: " + msgObject);
            if (msgObject.server_id != null) {
                LOG.info("sending connect");

                session.getAsyncRemote().sendText("{\"msg\": \"connect\",\"version\": \"1\",\"support\": [\"1\"]}", sendResult -> {
                    LOG.info("sendresult: " + sendResult);
                });
            } else if ("connected".equals(msgObject.msg)) {
                connectResult.complete(msgObject.session);
            } else if ("ping".equals(msgObject.msg)) {
                session.getAsyncRemote().sendText("{\"msg\":\"ping\"}", result -> LOG.fine("sent pong: " + result.isOK()));
            } else if (futureResults.containsKey(msgObject.id)) {
                boolean complete = futureResults.remove(msgObject.id).completeAndMap(msgObject);
                if (!complete) {
                    LOG.warning("future result was already completed: " + msgObject);
                }
            } else if (msgObject.fields != null && subscriptionResults.containsKey(msgObject.fields.get("eventName"))) {
                subscriptionResults.get(msgObject.fields.get("eventName")).next(msgObject);
            } else {
                LOG.warning("Unhandled message: " + message);
            }
        }

    }

}
