package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.HashMap;
import java.util.Map;

public class LoginOAuthParam {
    private final Map<String, Object> oauth = new HashMap<>();

    public LoginOAuthParam(String credentialToken, String credentialSecret) {
        this.oauth.put("credentialToken", credentialToken);
        this.oauth.put("credentialSecret", credentialSecret);
    }

}
