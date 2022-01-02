package com.github.daniel_sc.rocketchat.modern_client.response.livechat;

import com.github.daniel_sc.rocketchat.modern_client.response.User;

import java.util.List;

public class InitialData {
 public Boolean enabled; //  true
 public String title; //  'Rocket.Chat'
 public String color; //  '#C1272D'
 public Boolean registrationForm; //  true
 public User visitor; //  undefined
 public List<Trigger> triggers; //  []
 public List<Department> departments; //  []
 public Boolean allowSwitchingDepartments; //  true
 public Boolean online; //  true
 public String offlineColor; //  '#666666'
 public String offlineMessage; //  'We are not online right now. Please leave us a message:'
 public String offlineSuccessMessage; //  ''
 public String offlineUnavailableMessage; //  ''
 public Boolean displayOfflineForm; //  true
 public Boolean videoCall; //  false
 public String offlineTitle; //  'Leave a message'
 public String language; //  'en'
 public Boolean transcript; //  false
 public String transcriptMessage; //  'Would you like a copy of this chat emailed?'
 public String conversationFinishedMessage; //  'Conversation finished'
 public Boolean nameFieldRegistrationForm; //  true
 public Boolean emailFieldRegistrationForm; //  true

 @Override
 public String toString() {
  return "InitialData{" +
          "enabled=" + enabled +
          ", title='" + title + '\'' +
          ", color='" + color + '\'' +
          ", registrationForm=" + registrationForm +
          ", visitor=" + visitor +
          ", triggers=" + triggers +
          ", departments=" + departments +
          ", allowSwitchingDepartments=" + allowSwitchingDepartments +
          ", online=" + online +
          ", offlineColor='" + offlineColor + '\'' +
          ", offlineMessage='" + offlineMessage + '\'' +
          ", offlineSuccessMessage='" + offlineSuccessMessage + '\'' +
          ", offlineUnavailableMessage='" + offlineUnavailableMessage + '\'' +
          ", displayOfflineForm=" + displayOfflineForm +
          ", videoCall=" + videoCall +
          ", offlineTitle='" + offlineTitle + '\'' +
          ", language='" + language + '\'' +
          ", transcript=" + transcript +
          ", transcriptMessage='" + transcriptMessage + '\'' +
          ", conversationFinishedMessage='" + conversationFinishedMessage + '\'' +
          ", nameFieldRegistrationForm=" + nameFieldRegistrationForm +
          ", emailFieldRegistrationForm=" + emailFieldRegistrationForm +
          '}';
 }
}
