package com.websocket;

public class User {
    public String _id;
    public String username;
    public String name;

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
