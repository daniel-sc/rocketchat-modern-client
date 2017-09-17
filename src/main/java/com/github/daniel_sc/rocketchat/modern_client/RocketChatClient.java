package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.request.*;
import com.github.daniel_sc.rocketchat.modern_client.response.ChatMessage;
import com.github.daniel_sc.rocketchat.modern_client.response.GenericAnswer;
import com.github.daniel_sc.rocketchat.modern_client.response.Subscription;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RocketChatClient implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(RocketChatClient.class.getName());

    private static final Gson GSON = new Gson();

    private static final Map<String, CompletableFutureWithMapper<?>> futureResults = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ObservableSubjectWithMapper<?>> subscriptionResults = new ConcurrentHashMap<>();

    private Session session;
    private final String url;
    private final CompletableFuture<String> connectResult = new CompletableFuture<>();
    private final CompletableFuture<String> login;

    public RocketChatClient(String url, String user, String password) {
        this.url = url;
        login = login(user, password);
        LOG.fine("Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
    }

    protected CompletableFuture<String> connect() {
        if (!connectResult.isDone()) { // TODO check if connection in progress
            try {
                LOG.info("connecting to " + url);
                WSClient clientEndpoint = new WSClient();
                session = ContainerProvider.getWebSocketContainer().connectToServer(clientEndpoint, URI.create(url));
                LOG.fine("created session: " + session);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return connectResult;
    }

    protected CompletableFuture<String> login(String user, String password) {
        return connect().thenCompose(session -> sendDirect(new MethodRequest("login", new LoginParam(user, password)),
                r -> (String) r.resultAsMap().get("token")));
    }

    public CompletableFuture<List<Subscription>> getSubscriptions() {
        return send(new MethodRequest("subscriptions/get"),
                genericAnswer -> {
                    JsonElement jsonElement = GSON.toJsonTree(genericAnswer.resultAsList());
                    return GSON.fromJson(jsonElement, new TypeToken<List<Subscription>>() {
                    }.getType());
                });
    }

    protected <T> CompletableFuture<T> send(IRequest request, Function<GenericAnswer, T> answerMapper) {
        return login.thenCompose(token -> sendDirect(request, answerMapper));

    }

    protected <T> CompletableFuture<T> sendDirect(IRequest request, Function<GenericAnswer, T> answerMapper) {
        CompletableFutureWithMapper<T> result = new CompletableFutureWithMapper<>(answerMapper);
        futureResults.put(request.getId(), result);
        String requestString = GSON.toJson(request);
        LOG.fine("REQUEST: " + requestString);
        session.getAsyncRemote().sendText(requestString, sendResult -> handleSendResult(sendResult, result));
        return result;
    }

    public CompletableFuture<ChatMessage> sendMessage(String msg, String rid) {
        MethodRequest request = new MethodRequest("sendMessage", new SendMessageParam(msg, rid));
        return send(request, r -> GSON.fromJson(GSON.toJsonTree(r.result), ChatMessage.class));
    }


    /**
     * Subscribes to chat room messages. Subscription is automatically managed,
     * i.e. the chat room subscription starts when the first subscriber is attached
     * to he subject and the subscription is cancelled once the last subscriber of
     * the subject is disposed.
     *
     * @param rid room id
     * @return lazily initialized stream of chat room messages
     */
    public Observable<ChatMessage> streamRoomMessages(String rid) {
        // TODO refactor with further stream methods
        subscriptionResults.computeIfAbsent(rid, roomId -> {
            LOG.fine("creating new subscription observable");
            SubscriptionRequest request = new SubscriptionRequest("stream-room-messages", rid, false);
            PublishSubject<ChatMessage> subject = PublishSubject.create();
            Observable<ChatMessage> observable = subject.doFinally(() -> {
                LOG.fine("cancelling subscription");
                send(new UnsubscribeRequest(request.getId()), Function.identity())
                        .handleAsync((r, error) -> {
                            LOG.fine("handling unsubscribe: result=" + r + ", error=" + error);
                            if (error != null) {
                                LOG.log(Level.WARNING, "Failed to unsubscribe: ", error);
                            }
                            subscriptionResults.remove(rid);
                            return r;
                        });
            }).share();

            send(request, Function.identity())
                    .handleAsync((r, error) -> {
                        LOG.fine("handling subscribe: result=" + r + ", error=" + error);
                        if (error != null) {
                            subject.onError(error);
                        }
                        return r;
                    });

            return new ObservableSubjectWithMapper<>(subject, observable,
                    r -> GSON.fromJson(GSON.toJsonTree(((List<?>) r.fields.get("args")).get(0)), ChatMessage.class));
        });
        return (Observable<ChatMessage>) subscriptionResults.get(rid).getObservable();
    }

    private static void handleSendResult(SendResult sendResult, CompletableFuture<?> result) {
        if (!sendResult.isOK()) {
            result.completeExceptionally(new SendFailedException(sendResult));
        }
    }

    @Override
    public void close() {
        LOG.info("closing client.."); // TODO fine
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Could not close session: ", e);
            }
        }
        futureResults.forEach((id, future) -> future.completeExceptionally(new RuntimeException("client closed")));
        subscriptionResults.forEach((id, observerAndMapper) -> observerAndMapper.getSubject().onError(new RuntimeException("client closed")));
    }

    @ClientEndpoint
    public class WSClient {

        public WSClient() {
            LOG.info("created WSClient");
        }

        @OnMessage
        public void onMessage(String message) {
            LOG.info("Received msg: " + message);
            GenericAnswer msgObject = GSON.fromJson(message, GenericAnswer.class);
            if (msgObject.server_id != null) {
                LOG.fine("sending connect");
                session.getAsyncRemote().sendText("{\"msg\": \"connect\",\"version\": \"1\",\"support\": [\"1\"]}", sendResult -> {
                    LOG.fine("connect ack: " + sendResult.isOK());
                });
            } else if ("connected".equals(msgObject.msg)) {
                connectResult.complete(msgObject.session);
            } else if ("ping".equals(msgObject.msg)) {
                session.getAsyncRemote().sendText("{\"msg\":\"ping\"}", result -> LOG.fine("sent pong: " + result.isOK()));
            } else if (msgObject.id != null && futureResults.containsKey(msgObject.id)) {
                boolean complete = futureResults.remove(msgObject.id).completeAndMap(msgObject);
                if (!complete) {
                    LOG.warning("future result was already completed: " + msgObject);
                }
            } else if (msgObject.fields != null
                    && msgObject.fields.get("eventName") != null
                    && subscriptionResults.containsKey(msgObject.fields.get("eventName"))) {
                subscriptionResults.get(msgObject.fields.get("eventName")).next(msgObject);
            } else {
                LOG.warning("Unhandled message: " + message);
            }
        }

    }

}
