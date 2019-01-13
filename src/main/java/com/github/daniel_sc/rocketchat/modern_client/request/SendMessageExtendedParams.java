package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.UUID;

public class SendMessageExtendedParams {
    public final String msg;
    public final String rid;
    public final String _id;
    public final String alias;
    public final String avatar;

    public SendMessageExtendedParams(String msg, String rid, String alias, String avatar) {
        this.msg = msg;
        this.rid = rid;
        this.alias = alias;
        this.avatar = avatar;
        this._id = UUID.randomUUID().toString();
    }    
    
}
