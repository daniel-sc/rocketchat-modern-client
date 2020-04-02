package com.github.daniel_sc.rocketchat.modern_client.api;

public class ReactParam {

	/**
	 * [REQUIRED] The ID of the message to react to
	 */
    public final String messageId;

    /**
     * [REQUIRED] The Emoji to add or remove
     */
    public final String emoji;

    /**
     * [REQUIRED] true to add the emoji, false to remove it
     */
    public final boolean shouldReact;

	public ReactParam(String messageId, String emoji, boolean shouldReact) {
		super();
        this.messageId = messageId;
        this.emoji = emoji;
        this.shouldReact = shouldReact;
    }

	public static ReactParam forReaction(String messageId, String emoji, boolean shouldReact) {
        return new ReactParam(messageId, emoji, shouldReact);
    }

}
