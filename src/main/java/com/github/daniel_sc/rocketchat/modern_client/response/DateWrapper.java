package com.github.daniel_sc.rocketchat.modern_client.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DateWrapper {
    @SerializedName("$date")
    public Date date;
}
