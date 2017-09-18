package com.github.daniel_sc.rocketchat.modern_client.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class GenericAnswer {
    public Integer server_id;
    public String msg;
    public String session;
    public String id;
    public String collection;
    public Map<String, ?> fields;
    public Map<String, ?> error;
    public JsonElement result;

    @Override
    public String toString() {
        return "GenericAnswer{" +
                "server_id=" + server_id +
                ", msg='" + msg + '\'' +
                ", session='" + session + '\'' +
                ", id='" + id + '\'' +
                ", error=" + error +
                ", result=" + result +
                '}';
    }
}
