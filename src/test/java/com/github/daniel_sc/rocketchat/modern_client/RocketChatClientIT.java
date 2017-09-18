package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.ChatMessage;
import com.github.daniel_sc.rocketchat.modern_client.response.Permission;
import com.github.daniel_sc.rocketchat.modern_client.response.Room;
import com.github.daniel_sc.rocketchat.modern_client.response.Subscription;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableReplay;
import io.reactivex.observables.ConnectableObservable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class RocketChatClientIT {

    private static final Logger LOG = Logger.getLogger(RocketChatClientIT.class.getName());

    private static final String PASSWORD = "testuserrocks";
    private static final String USER = "testuserrocks";
    private static final String URL = "wss://demo.rocket.chat:443/websocket";
    //private static final String URL = "ws://localhost:3000/websocket";
    private static final int DEFAULT_TIMEOUT = 20000;
    public static final String DEFAULT_ROOM = "PRIVATETESTGROUP";

    private RocketChatClient client;

    @Before
    public void setUp() {
        Logger.getLogger(RocketChatClient.class.getName()).setLevel(Level.FINE);
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(Level.FINE);
        }
        client = new RocketChatClient(URL, USER, PASSWORD, Executors.newFixedThreadPool(2));
    }

    @After
    public void tearDown() {
        // in reality one should use try-with-resource
        client.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetSubscriptions() {
        LOG.info("start testGetSubscriptions..");
        List<Subscription> subscriptions = client.getSubscriptions().join();

        LOG.info("subscriptions: " + subscriptions);
        assertNotNull(subscriptions);
        assertNotNull(subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().orElse(null));
        assertNull(subscriptions.stream().filter(s -> s.name.equals("non existing group")).findFirst().orElse(null));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetRooms() {
        LOG.info("start testGetRooms..");
        List<Room> rooms = client.getRooms().join();

        LOG.info("rooms: " + rooms);
        assertNotNull(rooms);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testSendMessage() {
        LOG.info("start testSendMessage..");
        ChatMessage msg = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get())
                .thenCompose(room -> client.sendMessage("TEST modern sdk", room.rid))
                .join();

        assertNotNull(msg);
        assertEquals("TEST modern sdk", msg.msg);
        assertEquals(Collections.emptyMap(), client.futureResults);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testStreamMessages() throws InterruptedException {
        LOG.info("start testStreamMessages..");
        CompletableFuture<Subscription> subscription = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get());


        // wrap in replay, so we can first send a message and won't miss this
        ConnectableObservable<ChatMessage> msgStream = ObservableReplay.createFrom(subscription.thenApply(room -> client.streamRoomMessages(room.rid))
                .join());
        Disposable streamDispose = msgStream.connect();

        String msgText = "TEST modern sdk: stream input";
        subscription.thenCompose(room -> client.sendMessage(msgText, room.rid))
                .join();

        ChatMessage receivedMsg = msgStream.blockingFirst();
        streamDispose.dispose();

        assertNotNull(receivedMsg);
        assertEquals(msgText, receivedMsg.msg);
        assertEquals(Collections.emptyMap(), client.subscriptionResults);
    }


    @Test(timeout = DEFAULT_TIMEOUT)
    public void testReturnsSameInstance() {
        LOG.info("start testReturnsSameInstance..");
        CompletableFuture<Subscription> subscription = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get());
        String rid = subscription.join().rid;

        assertEquals(client.streamRoomMessages(rid), client.streamRoomMessages(rid));
    }

    @Test(timeout = DEFAULT_TIMEOUT * 2)
    public void testParallelStreamMessages() throws Exception {
        LOG.info("start testParallelStreamMessages..");
        CompletableFuture<Subscription> subscription = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get());
        String rid = subscription.join().rid;

        // wrap in replay, so we can first send a message and won't miss this
        ConnectableObservable<ChatMessage> msgStream1 = client.streamRoomMessages(rid).take(2).replay();
        msgStream1.connect();
        ConnectableObservable<ChatMessage> msgStream2 = client.streamRoomMessages(rid).take(4).replay();
        msgStream2.connect();

        for (int i = 0; i < 6; i++) {
            client.sendMessage("TEST parallel stream " + i, rid).join();
        }

        List<ChatMessage> messages1 = msgStream1.toList().blockingGet();
        LOG.info("messages 1: " + messages1);
        List<ChatMessage> messages2 = msgStream2.toList().blockingGet();
        LOG.info("messages 2: " + messages2);

        assertEquals(2, messages1.size());
        assertEquals(4, messages2.size());

        Thread.sleep(2000);

        // send some more messages, that should not be received (except for ack!):
        for (int i = 6; i < 10; i++) {
            client.sendMessage("TEST parallel stream " + i, rid).join();
        }
    }

    @Test(expected = Exception.class, timeout = DEFAULT_TIMEOUT)
    public void testNonExistingRoom() {
        LOG.info("start testNonExistingRoom..");
        ChatMessage result = client.sendMessage("test", "non existing room id").join();
        LOG.info("result=" + result);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetPermissions() {
        LOG.info("start testGetPermissions..");
        List<Permission> permissions = client.getPermissions().join();
        LOG.info("permissions: " + permissions);
        assertNotNull(permissions);
        assertTrue(permissions.size() > 0);
        Permission first = permissions.get(0);
        assertNotNull(first.id);
        assertNotNull(first.loki);
        assertNotNull(first.meta);
        assertNotNull(first.roles);
        assertNotNull(first.updatedAt);
        assertNotNull(first.updatedAt.date);
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = Exception.class)
    public void testConnectToWrongUrl() {
        LOG.info("start testConnectToWrongUrl..");
        try (RocketChatClient c = new RocketChatClient("ws://localhost:3001", null, null)) {
            fail("Expect client to fail construction!");
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = Exception.class)
    public void testWrongCredentials() {
        LOG.info("start testWrongCredentials..");
        try (RocketChatClient c = new RocketChatClient(URL, USER, "wrongpassword")) {
            c.sendMessage("test msg", "1").join();
            fail("Expect client to fail construction!");
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testDoubleClose() {
        LOG.info("start testDoubleClose..");
        client.getPermissions().join();
        client.close();
        client.close();
    }
}
