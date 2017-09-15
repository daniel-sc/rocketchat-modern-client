package com.github.daniel_sc.rocketchat.modern_client.request;

public class MethodRequest extends Request {

    public final String method;

    public MethodRequest(String method, Object... params) {
        super("method", params);
        this.method = method;
    }

}
