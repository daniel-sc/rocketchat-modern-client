package com.websocket;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static Object waitLock = new Object();

    public static void main(String[] args) throws Exception {
        String user = args[0];
        String password = args[1];
        String roomName = "mytest42";
        LOG.info("starting, user=" + user + " roomName=" + roomName);

        //try (RocketChatClient client = new RocketChatClient("ws://localhost:3000/websocket")) {
        try (RocketChatClient client = new RocketChatClient("wss://demo.rocket.chat:443/websocket")) {
            List<Subscription> subscriptions = client.connect()
                    .thenCompose(session -> client.login(user, password))
                    .thenCompose(loginResponse -> client.getSubscriptions())
                    .get();
            LOG.info("subscriptions: " + subscriptions);
            Subscription room = subscriptions.stream().filter(s -> s.name.equals(roomName)).findFirst().get();
            GenericAnswer sendMessageResult = client.sendMessage("Test", room.rid).get();
            LOG.info("sendMessageResult: " + sendMessageResult);
            AtomicInteger i1 = new AtomicInteger();
            client.streamRoomMessages(room.rid).forEachWhile(msg -> {
                LOG.info("received message 1: " + i1.get() + ": " + msg);
                return i1.incrementAndGet() < 4;
            });
            AtomicInteger i2 = new AtomicInteger();
            client.streamRoomMessages(room.rid).forEachWhile(msg -> {
                LOG.info("received message 2: " + i2.get() + ": " + msg);
                return i2.incrementAndGet() < 2;
            });

            wait4TerminateSignal();
        }
        LOG.info("done");
    }

    private static void wait4TerminateSignal() {
        synchronized (waitLock) {
            try {
                waitLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

}
