package com.github.daniel_sc.rocketchat.modern_client.response;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage {
    public String msg;
    public User u;
    public String rid;
    public String _id;
    public DateWrapper editedAt;
    public User editedBy;
    public Map<String,UserList> reactions = new HashMap<>();

    public ChatMessage(String msg, User u, String rid, DateWrapper editedAt, User editedBy, Map<String,UserList> reactions) {
        this.msg = msg;
        this.u = u;
        this.rid = rid;
        this.editedAt = editedAt;
        this.editedBy = editedBy;
        this.reactions = reactions;
    }

    public boolean isModified() {
        return editedAt != null && editedBy != null;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msg='" + msg + '\'' +
                ", u=" + u + '\'' +
                ", rid=" + rid + '\'' +
                ", _id=" + _id +
                '}';
    }
}
