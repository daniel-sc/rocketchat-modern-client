package com.rocketchat.modern_client.request;

public class UnsubscribeRequest extends Request {
    public UnsubscribeRequest(String rid) {
        super("unsub");
        id = rid;
    }
}
