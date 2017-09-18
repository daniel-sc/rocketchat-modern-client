package com.github.daniel_sc.rocketchat.modern_client.response;

import java.util.Date;

public class MetaData {
    public Integer revision;
    public Date created;
    public Integer version;
    public Date updated;

    @Override
    public String toString() {
        return "MetaData{" +
                "revision=" + revision +
                ", created=" + created +
                ", version=" + version +
                ", updated=" + updated +
                '}';
    }
}
