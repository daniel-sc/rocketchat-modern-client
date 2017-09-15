package com.github.daniel_sc.rocketchat.modern_client.request;


public class SubscriptionRequest extends Request {

    public final String name;

    public SubscriptionRequest(String name, Object... params) {
        super("sub", params);
        this.name = name;
    }
}
