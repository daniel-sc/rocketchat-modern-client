package com.github.daniel_sc.rocketchat.modern_client.response;

public class Subscription {
    public String name;
    /** roomId */
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
