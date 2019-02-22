package com.github.daniel_sc.rocketchat.modern_client.request;

/**
 * {@link} https://rocket.chat/docs/developer-guides/rest-api/chat/postmessage/#attachments-detail
 *
 */
public class AttachmentField {
	
	/**
	 * Whether this field should be a short field.
	 */
	public boolean _short = false;
	
	/**
	 * The title of this field.
	 */
	public final String title;
	
	/**
	 * The value of this field, displayed underneath the title value.
	 */
	public final String value;

	public AttachmentField(String title, String value) {
		this.title = title;
		this.value = value;
	}
	
	/*
	public AttachmentField(boolean _short, String title, String value) {
		this._short = _short;
		this.title = title;
		this.value = value;
	}		
	*/

	@Override
	public String toString() {
		return "AttachmentField [title=" + title + ", value=" + value + "]";
	}

	
}
