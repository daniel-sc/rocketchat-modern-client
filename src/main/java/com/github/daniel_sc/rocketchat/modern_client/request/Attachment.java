package com.github.daniel_sc.rocketchat.modern_client.request;

import java.util.List;

/**
 * {@link} https://rocket.chat/docs/developer-guides/rest-api/chat/postmessage/#attachments-detail
 *
 */
public class Attachment {
	
	/**
	 * The color you want the order on the left side to be, any value background-css supports.
	 */
	public String color = "";
	
	/**
	 * The text to display for this attachment, it is different than the message’s text.
	 */
	public String text = "";

	/**
	 * An image that displays to the left of the text, looks better when this is relatively small.
	 */
	public String thumbUrl = "";
	
	/**
	 * Causes the image, audio, and video sections to be hiding when collapsed is true.
	 */
	public boolean collapsed = false;
	
	/**
	 * Name of the author.
	 */
	public String author_name = "";
	
	/**
	 * Providing this makes the author name clickable and points to this link.
	 */
	public String author_link = "";
	
	/**
	 * Displays a tiny icon to the left of the Author’s name.
	 */
	public String author_icon = "";
	
	/**
	 * Title to display for this attachment, displays under the author.
	 */
	public String title = "";
	
	/**
	 * Providing this makes the title clickable, pointing to this link.
	 */
	public String title_link = "";
	
	/**
	 * When this is true, a download icon appears and clicking this saves the link to file.
	 */
	public boolean title_link_download = true;
	
	/**
	 * The image to display, will be “big” and easy to see.
	 */
	public String image_url = "";
	
	/**
	 * Audio file to play, only supports what html audio does.
	 */
	public String audio_url = "";
	
	/**
	 * Video file to play, only supports what html video does.
	 */
	public String video_url = "";
	
	/**
	 * An array of Attachment Field Objects.
	 */
	public List<AttachmentField> attachmentFields;
	
	public Attachment() {
	}
	
	public Attachment(String color, String text, String thumbUrl, boolean collapsed, String author_name,
			String author_link, String author_icon, String title, String title_link, boolean title_link_download,
			String image_url, String audio_url, String video_url, List<AttachmentField> attachmentFields) {
		this.color = color;
		this.text = text;
		this.thumbUrl = thumbUrl;
		this.collapsed = collapsed;
		this.author_name = author_name;
		this.author_link = author_link;
		this.author_icon = author_icon;
		this.title = title;
		this.title_link = title_link;
		this.title_link_download = title_link_download;
		this.image_url = image_url;
		this.audio_url = audio_url;
		this.video_url = video_url;
		this.attachmentFields = attachmentFields;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public String getAuthor_link() {
		return author_link;
	}

	public void setAuthor_link(String author_link) {
		this.author_link = author_link;
	}

	public String getAuthor_icon() {
		return author_icon;
	}

	public void setAuthor_icon(String author_icon) {
		this.author_icon = author_icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle_link() {
		return title_link;
	}

	public void setTitle_link(String title_link) {
		this.title_link = title_link;
	}

	public boolean isTitle_link_download() {
		return title_link_download;
	}

	public void setTitle_link_download(boolean title_link_download) {
		this.title_link_download = title_link_download;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getAudio_url() {
		return audio_url;
	}

	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public List<AttachmentField> getAttachmentFields() {
		return attachmentFields;
	}

	public void setAttachmentFields(List<AttachmentField> attachmentFields) {
		this.attachmentFields = attachmentFields;
	}

	@Override
	public String toString() {
		return "Attachments [color=" + color + ", text=" + text + ", thumbUrl=" + thumbUrl + ", collapsed=" + collapsed
				+ ", author_name=" + author_name + ", author_link=" + author_link + ", author_icon=" + author_icon
				+ ", title=" + title + ", title_link=" + title_link + ", title_link_download=" + title_link_download
				+ ", image_url=" + image_url + ", audio_url=" + audio_url + ", video_url=" + video_url + "]";
	}
	
	

}
