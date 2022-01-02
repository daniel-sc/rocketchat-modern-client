package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import com.github.daniel_sc.rocketchat.modern_client.response.DateWrapper;
import com.github.daniel_sc.rocketchat.modern_client.response.User;

public class LiveChatMessage {
    public String msg;
    public User u;
    public String rid;
    public String _id;
    public String token;
    public String alias;
    public DateWrapper _updatedAt;
    public DateWrapper ts;
    public Boolean newRoom;
    public Boolean showConnecting;

    @Override
    public String toString() {
        return "LiveChatMessage{" +
                "msg='" + msg + '\'' +
                ", u=" + u +
                ", rid='" + rid + '\'' +
                ", _id='" + _id + '\'' +
                ", token='" + token + '\'' +
                ", alias='" + alias + '\'' +
                ", _updatedAt=" + _updatedAt +
                ", ts=" + ts +
                ", newRoom=" + newRoom +
                ", showConnecting=" + showConnecting +
                '}';
    }
}
