package com.github.daniel_sc.rocketchat.modern_client.request;

import java.time.Instant;
import java.util.List;

import com.google.gson.annotations.SerializedName;

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
	 * Displays the time next to the text portion.
	 * 2016-12-09T16:53:06.761Z
	 */
	@SerializedName("ts")
	public String iso8601Date = Instant.now().toString();

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
	@SerializedName("author_name")
	public String authorName = "";
	
	/**
	 * Providing this makes the author name clickable and points to this link.
	 */
	@SerializedName("author_link")
	public String authorLink = "";
	
	/**
	 * Displays a tiny icon to the left of the Author’s name.
	 */
	@SerializedName("author_icon")
	public String authorIcon = "";
	
	/**
	 * Title to display for this attachment, displays under the author.
	 */
	public String title = "";
	
	/**
	 * Providing this makes the title clickable, pointing to this link.
	 */
	@SerializedName("title_link")
	public String titleLink = "";
	
	/**
	 * When this is true, a download icon appears and clicking this saves the link to file.
	 */
	@SerializedName("title_link_download")
	public boolean titleLinkDownload = true;
	
	/**
	 * The image to display, will be “big” and easy to see.
	 */
	@SerializedName("image_url")
	public String imageUrl = "";
	
	/**
	 * Audio file to play, only supports what html audio does.
	 */
	@SerializedName("audio_url")
	public String audioUrl = "";
	
	/**
	 * Video file to play, only supports what html video does.
	 */
	@SerializedName("video_url")
	public String videoUrl = "";
	
	/**
	 * An array of Attachment Field Objects.
	 */
	public List<AttachmentField> fields;
	
	public Attachment() {
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

	public String getIso8601Date() {
		return iso8601Date;
	}

	public void setIso8601Date(String iso8601Date) {
		this.iso8601Date = iso8601Date;
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

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorLink() {
		return authorLink;
	}

	public void setAuthorLink(String authorLink) {
		this.authorLink = authorLink;
	}

	public String getAuthorIcon() {
		return authorIcon;
	}

	public void setAuthorIcon(String authorIcon) {
		this.authorIcon = authorIcon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleLink() {
		return titleLink;
	}

	public void setTitleLink(String titleLink) {
		this.titleLink = titleLink;
	}

	public boolean isTitleLinkDownload() {
		return titleLinkDownload;
	}

	public void setTitleLinkDownload(boolean titleLinkDownload) {
		this.titleLinkDownload = titleLinkDownload;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public List<AttachmentField> getFields() {
		return fields;
	}

	public void setFields(List<AttachmentField> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Attachment [color=" + color + ", text=" + text + ", iso8601Date=" + iso8601Date + ", thumbUrl="
				+ thumbUrl + ", collapsed=" + collapsed + ", authorName=" + authorName + ", authorLink=" + authorLink
				+ ", authorIcon=" + authorIcon + ", title=" + title + ", titleLink=" + titleLink
				+ ", titleLinkDownload=" + titleLinkDownload + ", imageUrl=" + imageUrl + ", audioUrl=" + audioUrl
				+ ", videoUrl=" + videoUrl + ", fields=" + fields + "]";
	}
	
}
