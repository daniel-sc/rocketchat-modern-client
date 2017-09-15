package com.github.daniel_sc.rocketchat.modern_client.request;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginParam {
    private final Map<String, String> user = new HashMap<>();
    private final Map<String, String> password = new HashMap<>();

    public LoginParam(String user, String password) {
        this.user.put("username", user);
        Charset charset = Charset.forName("UTF-8");
        this.password.put("digest", getDigest(password));
        this.password.put("algorithm", "sha-256");
    }

    public static String getDigest(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
