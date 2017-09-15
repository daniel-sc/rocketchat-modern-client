package com.github.daniel_sc.rocketchat.modern_client.response;

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
