package com.github.daniel_sc.rocketchat.modern_client.request;

public class UpdateMessage {
    public final String msg;
    public final String _id;

    public UpdateMessage(String msg, String _id) {
        this.msg = msg;
        this._id = _id;
    }
}
