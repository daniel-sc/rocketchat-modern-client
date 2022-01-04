package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import java.util.Map;

public class Action {
    public String name;
    public Map<String, String> params;

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", params=" + params +
                '}';
    }
}
