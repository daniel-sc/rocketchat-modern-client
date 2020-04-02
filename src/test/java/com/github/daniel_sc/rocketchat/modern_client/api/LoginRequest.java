package com.github.daniel_sc.rocketchat.modern_client.api;

public class LoginRequest {
    public final String user;
    public final String password;

    public LoginRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
