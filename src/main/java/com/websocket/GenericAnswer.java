package com.websocket;

import java.util.List;
import java.util.Map;

public class GenericAnswer {
    public Integer server_id;
    public String msg;
    public String session;
    public String id;
    public String collection;
    public Map<String, ?> fields;
    public Map<String, ?> error;
    public Object result;

    public Map<String, ?> resultAsMap() {
        return (Map<String, ?>) result;
    }

    public List<?> resultAsList() {
        return (List<?>) result;
    }


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
