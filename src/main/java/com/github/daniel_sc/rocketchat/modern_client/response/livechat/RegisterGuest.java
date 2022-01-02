package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

public class RegisterGuest {
    public String userId;
    public Visitor visitor;

    @Override
    public String toString() {
        return "RegisterGuest{" +
                "userId='" + userId + '\'' +
                ", visitor=" + visitor +
                '}';
    }
}
