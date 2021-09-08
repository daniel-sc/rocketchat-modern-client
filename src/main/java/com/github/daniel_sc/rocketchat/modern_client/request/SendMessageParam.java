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
     *
     * @see <a href="https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage#important">https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage#important</a>
     */
    public final String alias;

    /**
     * If provided, this will make the avatar use the provided image url.
     *
     * @see <a href="https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage#important">https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage#important</a>
     */
    public final String avatar;

    /**
     * If provided, this will make the avatar on this message be an emoji.
     */
    public final String emoji;

    /**
     * Boolean that states whether or not this message should be grouped together with other messages from the same user
     */
    public Boolean groupable;

    public List<Attachment> attachments;

	public SendMessageParam(String msg, String rid, String alias, String avatar, String emoji,
			Boolean groupable, List<Attachment> attachments, String msgId) {
		super();
		this.msg = msg;
		this.rid = rid;
		this._id = msgId;
		this.alias = alias;
		this.avatar = avatar;
		this.emoji = emoji;
		this.groupable = groupable;
		this.attachments = attachments;
	}

	public static SendMessageParam forSendMessage(String msg, String rid, String alias, String avatar, String emoji, Boolean groupable, List<Attachment> attachments) {
        return new SendMessageParam(msg, rid, alias, avatar, emoji, groupable, attachments, UUID.randomUUID().toString());
    }

    public static SendMessageParam forUpdate(String msgId, String msg, String rid, String alias, String avatar, String emoji, Boolean groupable, List<Attachment> attachments) {
        return new SendMessageParam(msg, rid, alias, avatar, emoji, groupable, attachments, msgId);
    }

}
