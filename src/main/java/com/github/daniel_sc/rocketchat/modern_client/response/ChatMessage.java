package com.github.daniel_sc.rocketchat.modern_client.response;

public class ChatMessage {
    public String msg;
    public User u;
    public String _id;

    public ChatMessage(String msg, User u) {
        this.msg = msg;
        this.u = u;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msg='" + msg + '\'' +
                ", u=" + u + '\'' +
                ", _id=" + _id + 
                '}';
    }
}
