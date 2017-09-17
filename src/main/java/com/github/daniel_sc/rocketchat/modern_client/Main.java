package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.ChatMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Logger.getLogger(RocketChatClient.class.getName()).setLevel(Level.FINE);
        LOG.info("start");
        try (RocketChatClient client = new RocketChatClient("ws://localhost:3000/websocket", "testuserrocks", "testuserrocks")) {
            String rid = client.getSubscriptions()
                    .thenApply(subscriptions -> subscriptions.stream()
                            .filter(s -> s.name.equalsIgnoreCase("mytest42"))
                            .findFirst()
                            .get().rid)
                    .join();
            LOG.info("rid=" + rid);
            ChatMessage msg = client.sendMessage("test message without join", "asdf").join();
            LOG.info("sent msg: " + msg);
        }
        LOG.info("done");
    }
}
