package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import com.github.daniel_sc.rocketchat.modern_client.response.DateWrapper;

public class Department {
      public String _id; // "4swtja84kmn5WCdwL"
      public Boolean enabled; // true
      public String name; // "My Department"
      public String description; // "I have no description for this department"
      public Long numAgents; // 1
      public Boolean showOnRegistration; // true
      public DateWrapper _updatedAt; // "2016-12-06T17:19:18.138Z"

      @Override
      public String toString() {
            return "Department{" +
                    "_id='" + _id + '\'' +
                    ", enabled=" + enabled +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", numAgents=" + numAgents +
                    ", showOnRegistration=" + showOnRegistration +
                    ", _updatedAt=" + _updatedAt +
                    '}';
      }
}
