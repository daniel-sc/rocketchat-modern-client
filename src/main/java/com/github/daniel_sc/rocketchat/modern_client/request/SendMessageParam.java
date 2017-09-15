package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.UUID;

public class SendMessageParam {
    public final String msg;
    public final String rid;
    public final String _id;

    public SendMessageParam(String msg, String rid) {
        this.msg = msg;
        this.rid = rid;
        this._id = UUID.randomUUID().toString();
    }
}
