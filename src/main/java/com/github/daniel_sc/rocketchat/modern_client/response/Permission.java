package com.github.daniel_sc.rocketchat.modern_client.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Permission {
    @SerializedName("_id")
    public String id;
    public List<String> roles;
    @SerializedName("_updatedAt")
    public DateWrapper updatedAt;
    @SerializedName("$loki")
    public Integer loki;
    public MetaData meta;

    @Override
    public String toString() {
        return "Permission{" +
                "id='" + id + '\'' +
                ", roles=" + roles +
                ", updatedAt=" + updatedAt +
                ", loki=" + loki +
                ", meta=" + meta +
                '}';
    }
}
