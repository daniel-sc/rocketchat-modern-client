package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.request.Attachment;
import com.github.daniel_sc.rocketchat.modern_client.request.AttachmentField;
import com.github.daniel_sc.rocketchat.modern_client.request.LoginParam;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class RocketChatClientIT {

    private static final Logger LOG = Logger.getLogger(RocketChatClientIT.class.getName());

    private static final String PASSWORD = "testuserrocks";
    private static final String USER = "testuserrocks-0";
    private static final String URL = "wss://open.rocket.chat:443/websocket";
    //private static final String URL = "ws://localhost:3000/websocket";
    private static final int DEFAULT_TIMEOUT = 20000;
    public static final String DEFAULT_ROOM = "privatetestgroup-0";

    private RocketChatClient client;

    @Before
    public void setUp() {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1)); // prevent rate limit..
        client = new RocketChatClient(URL, new LoginParam(USER, PASSWORD), Executors.newFixedThreadPool(2));
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
    public void testGetUserId() {
        LOG.info("start testGetUserId..");
        assertNull("expected user id to be null immediately after client creation", client.getLoggedInUserId());

        // wait for connect:
        client.getSubscriptions().join();

        assertEquals("HoiYngmpiY6Y9DD8L", client.getLoggedInUserId());
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
    public void testExtendedSendMessage() {
        LOG.info("start testExtendedSendMessage..");
        ChatMessage msg = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get())
                .thenCompose(room -> client.sendMessageExtendedParams("TEST modern sdk: with alias", room.rid, "My-Alias", null, null, null, null))
                .join();

        assertNotNull(msg);
        assertEquals(Collections.emptyMap(), client.futureResults);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testUpdateMessage() {
        LOG.info("start testUpdateMessage..");
        ChatMessage msg = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get())
                .thenCompose(room -> client.sendMessage("TEST modern sdk (before update)", room.rid))
                .thenCompose(originalMessage -> client.updateMessage("TEST modern sdk (after update)", originalMessage._id))
                .join();

        assertNull(msg); // update result is null!
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

        assertNotNull(receivedMsg);
        assertEquals(msgText, receivedMsg.msg);
        assertNull(receivedMsg.editedBy);
        assertNull(receivedMsg.editedAt);
        assertFalse(receivedMsg.isModified());

        String msgTextModified = "TEST modern sdk: stream input (modified)";
        subscription.thenCompose(room -> client.updateMessage(msgTextModified, receivedMsg._id))
                .join();

        ChatMessage receivedModifiedMsg = msgStream.skip(1).blockingFirst();

        assertNotNull(receivedModifiedMsg);
        assertEquals(msgTextModified, receivedModifiedMsg.msg);
        assertNotNull(receivedModifiedMsg.editedBy);
        assertNotNull(receivedModifiedMsg.editedAt);
        assertTrue(receivedModifiedMsg.isModified());

        streamDispose.dispose();
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
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        ConnectableObservable<ChatMessage> msgStream2 = client.streamRoomMessages(rid).take(4).replay();
        msgStream2.connect();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));

        for (int i = 0; i < 5; i++) {
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
        for (int i = 5; i < 8; i++) {
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
        assertNotNull(first.roles);
        assertNotNull(first.updatedAt);
        assertNotNull(first.updatedAt.date);
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = Exception.class)
    public void testConnectToWrongUrl() {
        LOG.info("start testConnectToWrongUrl..");
        try (RocketChatClient c = new RocketChatClient("ws://localhost:3001", new LoginParam(null, null))) {
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

    @Test(timeout = DEFAULT_TIMEOUT, expected = Exception.class)
    public void testNullPassword() {
        LOG.info("start testWrongCredentials..");
        try (RocketChatClient c = new RocketChatClient(URL, new LoginParam(USER, null))) {
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

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testAttachment() {
        LOG.info("start testAttachment..");
        Attachment attachment = new Attachment();
        attachment.color = "#0000ff";
        attachment.text = "Test attachment message\nand next line wit **mark-down** formatting...";
        attachment.authorName = "daniel-sc";
        attachment.authorIcon = "https://avatars2.githubusercontent.com/u/117919?s=460&v=4";
        attachment.authorLink = "https://github.com/daniel-sc";
        attachment.title = "Attachment test attachment";
        attachment.titleLink = "https://www.google.com/?q=Attachment%20test%20attachment";
        attachment.fields = Collections.singletonList(new AttachmentField("test-field-title", "test-field-value"));

        ChatMessage msg = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get())
                .thenCompose(room -> client.sendMessageExtendedParams("TEST modern sdk: with attachment", room.rid, "My-Alias", null, null, null, Collections.singletonList(attachment)))
                .join();

        assertNotNull(msg);
        assertEquals(Collections.emptyMap(), client.futureResults);
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = RuntimeException.class)
    public void testConnectionClosedShouldTerminateStream() throws IOException {
        CompletableFuture<Subscription> subscription = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get());
        String rid = subscription.join().rid;

        // wrap in replay, so we can first send a message and won't miss this
        ConnectableObservable<ChatMessage> msgStream1 = client.streamRoomMessages(rid).take(2).replay();
        msgStream1.connect();
        client.session.join().close();

        msgStream1.blockingSubscribe();
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = CompletionException.class)
    public void testConnectionClosedShouldFailSendMessage() throws IOException {
        CompletableFuture<Subscription> subscription = client.getSubscriptions()
                .thenApply(subscriptions -> subscriptions.stream().filter(s -> s.name.equalsIgnoreCase(DEFAULT_ROOM)).findFirst().get());
        String rid = subscription.join().rid;

        client.session.join().close();

        client.sendMessage("testmsg", rid).join();
    }
}
