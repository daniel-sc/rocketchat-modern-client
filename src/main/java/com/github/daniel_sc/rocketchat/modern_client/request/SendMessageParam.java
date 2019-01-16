package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.UUID;

public class SendMessageParam {
    public final String msg;
    public final String rid;
    public final String _id;
    public final String alias;
    public final String avatar;

    private SendMessageParam(String msg, String rid, String alias, String avatar, String msgId) {
        this.msg = msg;
        this.rid = rid;
        this.alias = alias;
        this.avatar = avatar;
        this._id = msgId;
    }

    public static SendMessageParam forSendMessage(String msg, String rid, String alias, String avatar) {
        return new SendMessageParam(msg, rid, alias, avatar, UUID.randomUUID().toString());
    }

    public static SendMessageParam forUpdate(String msgId, String msg, String rid, String alias, String avatar) {
        return new SendMessageParam(msg, rid, alias, avatar, msgId);
    }
}
