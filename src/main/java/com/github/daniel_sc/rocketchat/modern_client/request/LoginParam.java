package com.github.daniel_sc.rocketchat.modern_client.request;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginParam {
    public final Map<String, String> user = new HashMap<>();
    public final Map<String, String> password = new HashMap<>();

    public LoginParam(String user, String password) {
        this.user.put("username", user);
        this.password.put("digest", getDigest(password));
        this.password.put("algorithm", "sha-256");
    }

    public static String getDigest(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
