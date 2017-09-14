package com.websocket;

public class Subscription {
    public String name;
    public String rid;
    public String _id;

    @Override
    public String toString() {
        return "Subscription{" +
                "name='" + name + '\'' +
                ", rid='" + rid + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }
}
