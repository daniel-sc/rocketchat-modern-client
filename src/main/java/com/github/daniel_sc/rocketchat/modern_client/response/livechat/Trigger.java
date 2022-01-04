package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import com.github.daniel_sc.rocketchat.modern_client.response.DateWrapper;

import java.util.List;

public class Trigger {
    public String _id; // "Lk52shJFYyb55trw8",
    public String name; // "test"
    public String description; // "test"
    public Boolean enabled; // true
    public Boolean runOnce; // true
    public List<Condition> conditions;
    public List<Action> actions;
    public DateWrapper _updatedAt;

    @Override
    public String toString() {
        return "Trigger{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", runOnce=" + runOnce +
                ", conditions=" + conditions +
                ", actions=" + actions +
                ", _updatedAt=" + _updatedAt +
                '}';
    }
}
