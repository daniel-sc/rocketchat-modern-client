package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import java.util.List;
import java.util.Map;

public class Visitor {
    public String name;
    public String token;
    public String username;
    public List<Map<String, String>> visitorEmails;

    @Override
    public String toString() {
        return "Visitor{" +
                "name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", visitorEmails=" + visitorEmails +
                '}';
    }
}
