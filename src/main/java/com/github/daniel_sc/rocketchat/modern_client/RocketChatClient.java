package com.github.daniel_sc.rocketchat.modern_client;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.SendResult;
import javax.websocket.Session;

import com.github.daniel_sc.rocketchat.modern_client.request.Attachment;
import com.github.daniel_sc.rocketchat.modern_client.request.IRequest;
import com.github.daniel_sc.rocketchat.modern_client.request.LoginParam;
import com.github.daniel_sc.rocketchat.modern_client.request.MethodRequest;
import com.github.daniel_sc.rocketchat.modern_client.request.SendMessageParam;
import com.github.daniel_sc.rocketchat.modern_client.request.SubscriptionRequest;
import com.github.daniel_sc.rocketchat.modern_client.request.UnsubscribeRequest;
import com.github.daniel_sc.rocketchat.modern_client.response.ChatMessage;
import com.github.daniel_sc.rocketchat.modern_client.response.GenericAnswer;
import com.github.daniel_sc.rocketchat.modern_client.response.Permission;
import com.github.daniel_sc.rocketchat.modern_client.response.Room;
import com.github.daniel_sc.rocketchat.modern_client.response.Subscription;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RocketChatClient implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(RocketChatClient.class.getName());

    protected static final Gson GSON = new GsonBuilder()
            // parse dates from long:
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
            .create();

    protected final Map<String, CompletableFutureWithMapper<?>> futureResults = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ObservableSubjectWithMapper<?>> subscriptionResults = new ConcurrentHashMap<>();

    protected final CompletableFuture<Session> session = new CompletableFuture<>();
    protected final String url;
    protected final CompletableFuture<String> connectResult = new CompletableFuture<>();
    protected final CompletableFuture<String> login;
    protected final Executor executor;

    public RocketChatClient(String url, String user, String password) {
        this(url, user, password, ForkJoinPool.commonPool());
    }

    public RocketChatClient(String url, String user, String password, Executor executor) {
        this.url = url;
        this.executor = executor;
        login = login(user, password);
    }

    protected CompletableFuture<String> connect() {
        if (!connectResult.isDone()) {
            try {
                LOG.info("connecting to " + url);
                WSClient clientEndpoint = new WSClient();
                session.complete(ContainerProvider.getWebSocketContainer().connectToServer(clientEndpoint, URI.create(url)));
                LOG.fine("created session: " + session.join());
            } catch (Exception e) {
                session.completeExceptionally(e);
                throw new IllegalStateException(e);
            }
        }
        return connectResult;
    }

    protected CompletableFuture<String> login(String user, String password) {
        return connect().thenComposeAsync(session -> sendDirect(new MethodRequest("login", new LoginParam(user, password)),
                failOnError(r -> r.result.getAsJsonObject().get("token").getAsString())), executor);
    }

    public CompletableFuture<List<Subscription>> getSubscriptions() {
        return send(new MethodRequest("subscriptions/get"),
                failOnError(genericAnswer -> {
                    JsonElement jsonElement = GSON.toJsonTree(genericAnswer.result);
                    return GSON.fromJson(jsonElement, new TypeToken<List<Subscription>>() {
                    }.getType());
                }));
    }

    public CompletableFuture<List<Room>> getRooms() {
        return send(new MethodRequest("rooms/get"),
                failOnError(genericAnswer -> {
                    JsonElement jsonElement = GSON.toJsonTree(genericAnswer.result);
                    return GSON.fromJson(jsonElement, new TypeToken<List<Room>>() {
                    }.getType());
                }));
    }

    protected <T> CompletableFuture<T> send(IRequest request, Function<GenericAnswer, T> answerMapper) {
        return login.thenComposeAsync(token -> sendDirect(request, answerMapper), executor);

    }

    protected <T> CompletableFuture<T> sendDirect(IRequest request, Function<GenericAnswer, T> answerMapper) {
        CompletableFutureWithMapper<T> result = new CompletableFutureWithMapper<>(answerMapper);
        futureResults.put(request.getId(), result);
        String requestString = GSON.toJson(request);
        LOG.fine("REQUEST: " + requestString);
        session.join().getAsyncRemote().sendText(requestString, sendResult -> handleSendResult(sendResult, result));
        return result;
    }

    public CompletableFuture<ChatMessage> sendMessage(String msg, String rid) {
        return sendMessageExtendedParams(msg, rid, null, null, null, null, null);
    }

    public CompletableFuture<ChatMessage> sendMessageExtendedParams(String msg, String rid, String alias, String avatar, String emoji, Boolean groupable, List<Attachment> attachments) {
        MethodRequest request = new MethodRequest("sendMessage", SendMessageParam.forSendMessage(msg, rid, alias, avatar, emoji, groupable, attachments));
        return send(request, failOnError(r -> {
            if (r.result == null) {
                throw new IllegalStateException("Message result is empty!");
            }
            return GSON.fromJson(r.result, ChatMessage.class);
        }));
    }

    public CompletableFuture<ChatMessage> updateMessage(String msg, String _id) {
        return updateMessageWithAttachments(msg, _id, null, null);
    }

    public CompletableFuture<ChatMessage> updateMessageWithAttachments(String msg, String _id, Boolean groupable, List<Attachment> attachments) {
        MethodRequest request = new MethodRequest("updateMessage", SendMessageParam.forUpdate(_id, msg, null, null, null, null, groupable, attachments));
        return send(request, failOnError(r -> GSON.fromJson(GSON.toJsonTree(r.result), ChatMessage.class)));
    }

    public CompletableFuture<List<Permission>> getPermissions() {
        return send(new MethodRequest("permissions/get"),
                failOnError(r -> GSON.fromJson(GSON.toJsonTree(r.result), new TypeToken<List<Permission>>() {
                }.getType())));
    }

    protected <T> Function<GenericAnswer, T> failOnError(Function<GenericAnswer, T> mapper) {
        return result -> {
            if (result.error != null) {
                throw new RuntimeException("Send message failed: " + GSON.toJson(result.error));
            } else {
                return mapper.apply(result);
            }
        };
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
                subscriptionResults.remove(rid);
                send(new UnsubscribeRequest(request.getId()), failOnError(Function.identity()))
                        .handleAsync((r, error) -> {
                            LOG.fine("handling unsubscribe: result=" + r + ", error=" + error);
                            if (error != null) {
                                // this happens when client is closed immediately after disposing observer..
                                LOG.log(Level.FINE, "Failed to unsubscribe: ", error);
                            }
                            return r;
                        }, executor);
            }).share();

            send(request, failOnError(Function.identity()))
                    .handleAsync((r, error) -> {
                        LOG.fine("handling subscribe: result=" + r + ", error=" + error);
                        if (error != null) {
                            subject.onError(error);
                        }
                        return r;
                    }, executor);

            return new ObservableSubjectWithMapper<>(subject, observable,
                    r -> GSON.fromJson(GSON.toJsonTree(((List<?>) r.fields.get("args")).get(0)), ChatMessage.class));
        });
        //noinspection unchecked
        return (Observable<ChatMessage>) subscriptionResults.get(rid).getObservable();
    }

    private static void handleSendResult(SendResult sendResult, CompletableFuture<?> result) {
        if (!sendResult.isOK()) {
            result.completeExceptionally(new SendFailedException(sendResult));
        }
    }

    @Override
    public void close() {
        LOG.fine("closing client..");
        try {
            session.join().close();
        } catch (IOException | CompletionException | CancellationException e) {
            LOG.log(Level.WARNING, "Could not close session: ", e);
        }
        futureResults.forEach((id, future) -> {
            // this can happen for future results of cancelled subscriptions or pong messages..
            LOG.fine("terminating open result id=" + id + ", future=" + future +
                    " (you might want to 'join()' some result before closing the client to prevent this)");
            future.completeExceptionally(new RuntimeException("client closed"));
        });
        subscriptionResults.forEach((id, observerAndMapper) -> {
            LOG.warning("terminating open subscription id=" + id + ", observable=" + observerAndMapper.getObservable() +
                    " (you might want to dispose all observers before closing the client to prevent this)");
            observerAndMapper.getSubject().onError(new RuntimeException("client closed"));
        });
    }

    @ClientEndpoint
    public class WSClient {

        public WSClient() {
            LOG.fine("created WSClient");
        }

        @SuppressWarnings({"unused", "SuspiciousMethodCalls"})
        @OnMessage
        public void onMessage(String message) {
            LOG.fine("Received msg: " + message);
            GenericAnswer msgObject = GSON.fromJson(message, GenericAnswer.class);
            if (msgObject.server_id != null) {
                LOG.fine("sending connect");
                session.join().getAsyncRemote().sendText("{\"msg\": \"connect\",\"version\": \"1\",\"support\": [\"1\"]}",
                        sendResult -> LOG.fine("connect ack: " + sendResult.isOK()));
            } else if ("connected".equals(msgObject.msg)) {
                connectResult.complete(msgObject.session);
            } else if ("ping".equals(msgObject.msg)) {
                session.join().getAsyncRemote().sendText("{\"msg\":\"ping\"}",
                        result -> LOG.fine("sent pong: " + result.isOK()));
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
