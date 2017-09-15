package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.ChatMessage;
import com.github.daniel_sc.rocketchat.modern_client.response.Subscription;
import io.reactivex.internal.operators.observable.ObservableReplay;
import io.reactivex.observables.ConnectableObservable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class RocketChatClientIT {

    private static final Logger LOG = Logger.getLogger(RocketChatClientIT.class.getName());

    private static final String PASSWORD = "testuserrocks";
    private static final String USER = "testuserrocks";
    private static final String URL = "wss://demo.rocket.chat:443/websocket";
    private static final int DEFAULT_TIMEOUT = 10000;

    private RocketChatClient client;

    @Before
    public void setUp() {
        client = new RocketChatClient(URL);
    }

    @After
    public void tearDown() {
        // in reality one should use try-with-resource
        client.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetSubscriptions() {
        List<Subscription> subscriptions = client.connect()
                .thenCompose(session -> client.login(USER, PASSWORD))
                .thenCompose(token -> client.getSubscriptions())
                .join();

        LOG.info("subscriptions: " + subscriptions);
        assertNotNull(subscriptions);
        assertNotNull(subscriptions.stream().filter(s -> s.name.equals("PRIVATETESTGROUP")).findFirst().orElse(null));
        assertNull(subscriptions.stream().filter(s -> s.name.equals("non existing group")).findFirst().orElse(null));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testSendMessage() {
        ChatMessage msg = client.connect()
                .thenCompose(session -> client.login(USER, PASSWORD))
                .thenCompose(token -> client.getSubscriptions())
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equals("PRIVATETESTGROUP")).findFirst().get())
                .thenCompose(room -> client.sendMessage("TEST modern sdk", room.rid))
                .join();

        assertNotNull(msg);
        assertEquals("TEST modern sdk", msg.msg);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testStreamMessages() throws InterruptedException {

        CompletableFuture<Subscription> subscription = client.connect()
                .thenCompose(session -> client.login(USER, PASSWORD))
                .thenCompose(token -> client.getSubscriptions())
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equals("PRIVATETESTGROUP")).findFirst().get());


        // wrap in replay, so we can first send a message and won't miss this
        ConnectableObservable<ChatMessage> msgStream = ObservableReplay.createFrom(subscription.thenApply(room -> client.streamRoomMessages(room.rid))
                .join());
        msgStream.connect();

        String msgText = "TEST modern sdk: stream input";
        subscription.thenCompose(room -> client.sendMessage(msgText, room.rid))
                .join();


        ChatMessage receivedMsg = msgStream.blockingFirst();
        assertNotNull(receivedMsg);
        assertEquals(msgText, receivedMsg.msg);
    }
}
