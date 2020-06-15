package com.github.daniel_sc.rocketchat.modern_client.request;

public class LoginTokenParam {

    private final String resume;

    public LoginTokenParam(String authToken) {
        this.resume = authToken;
    }

}
