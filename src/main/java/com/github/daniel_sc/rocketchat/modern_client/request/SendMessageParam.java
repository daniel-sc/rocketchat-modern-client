package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.List;
import java.util.UUID;

public class SendMessageParam {
	
	/**
	 * The text of the message to send, is optional because of attachments.
	 */
    public final String msg;
    
    /**
     * [REQUIRED] The room id of where the message is to be sent. 
     */
    public final String rid;
    
    public final String _id;
    
    /**
     * This will cause the message’s name to appear as the given alias, but your username will still display.
     */
    public final String alias;
    
    /**
     * If provided, this will make the avatar use the provided image url.
     */
    public final String avatar;
    
    public List<Attachment> attachments;

    private SendMessageParam(String msg, String rid, String alias, String avatar, List<Attachment> attachments, String msgId) {
        this.msg = msg;
        this.rid = rid;
        this.alias = alias;
        this.avatar = avatar;
        this.attachments = attachments;
        this._id = msgId;
    }

    public static SendMessageParam forSendMessage(String msg, String rid, String alias, String avatar, List<Attachment> attachments) {
        return new SendMessageParam(msg, rid, alias, avatar, attachments, UUID.randomUUID().toString());
    }

    public static SendMessageParam forUpdate(String msgId, String msg, String rid, String alias, String avatar, List<Attachment> attachments) {
        return new SendMessageParam(msg, rid, alias, avatar, attachments, msgId);
    }
}
