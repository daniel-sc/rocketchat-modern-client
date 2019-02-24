package com.github.daniel_sc.rocketchat.modern_client.request;

import com.google.gson.annotations.SerializedName;

/**
 * {@link} https://rocket.chat/docs/developer-guides/rest-api/chat/postmessage/#attachments-detail
 *
 */
public class AttachmentField {
	
	/**
	 * Whether this field should be a short field.	 * 
	 */
	@SerializedName("short")
	public final boolean _short;
	
	/**
	 * [REQUIRED] The title of this field.
	 */
	public final String title;
	
	/**
	 * [REQUIRED] The value of this field, displayed underneath the title value.
	 */
	public final String value;

	public AttachmentField(String title, String value) {
		this(false, title, value);
	}
	
	public AttachmentField(boolean _short, String title, String value) {
		this._short = _short;
		this.title = title;
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttachmentField [_short=" + _short + ", title=" + title + ", value=" + value + "]";
	}		

	
}
