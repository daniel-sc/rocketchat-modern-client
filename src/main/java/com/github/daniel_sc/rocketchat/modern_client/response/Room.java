package com.github.daniel_sc.rocketchat.modern_client.response;

import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("_id")
    public String id;
    public String name;
    public String topic;
    @SerializedName("ro")
    public Boolean readOnly;
    public DateWrapper jitsiTimeout;
    @SerializedName("t")
    public Room.RoomType type;
    @SerializedName("u")
    public User creator;

    public enum RoomType {
        /**
         * direct chat
         */
        @SerializedName("d")
        DIRECT_CHAT,
        /**
         * private chat
         */
        @SerializedName("p")
        PRIVATE_CHAT,
        /**
         * chat
         */
        @SerializedName("c")
        CHAT,
        /**
         * live chat
         */
        @SerializedName("l")
        LIVE_CHAT
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", readOnly=" + readOnly +
                ", jitsiTimeout=" + jitsiTimeout +
                ", type=" + type +
                ", creator=" + creator +
                '}';
    }
}
