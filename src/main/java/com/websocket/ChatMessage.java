package com.websocket;

public class ChatMessage {
    public String msg;
    public User u;

    public ChatMessage(String msg, User u) {
        this.msg = msg;
        this.u = u;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msg='" + msg + '\'' +
                ", u=" + u +
                '}';
    }
}
