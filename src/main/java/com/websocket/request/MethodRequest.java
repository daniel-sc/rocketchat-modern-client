package com.websocket.request;

public class MethodRequest extends com.websocket.request.Request {

    public final String method;

    public MethodRequest(String method, Object... params) {
        super("method", params);
        this.method = method;
    }

}
