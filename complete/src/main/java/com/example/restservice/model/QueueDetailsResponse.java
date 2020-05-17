package com.example.restservice.model;

import java.util.ArrayList;
import java.util.List;

public class QueueDetailsResponse {
  public static class User {
    String name;
    String contactNo;

    public User(String name, String contactNo) {
      this.name = name;
      this.contactNo = contactNo;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getContactNo() {
      return contactNo;
    }

    public void setContactNo(String contactNo) {
      this.contactNo = contactNo;
    }
  }

  String queueId;
  List<User> users;

  public String getQueueId() {
    return queueId;
  }

  public void setQueueId(String queueId) {
    this.queueId = queueId;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public QueueDetailsResponse(String queueId) {
    this.queueId = queueId;
    this.users = new ArrayList<>();
  }

  public void addUser(com.example.restservice.model.User user) {
    this.users.add(new User(user.name, user.contactNumber));
  }
}
