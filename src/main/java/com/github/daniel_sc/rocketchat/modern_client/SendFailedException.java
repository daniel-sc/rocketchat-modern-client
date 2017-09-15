package com.github.daniel_sc.rocketchat.modern_client;

import javax.websocket.SendResult;

public class SendFailedException extends RuntimeException {

    public final SendResult result;

    public SendFailedException(SendResult result) {
        this.result = result;
    }
}
