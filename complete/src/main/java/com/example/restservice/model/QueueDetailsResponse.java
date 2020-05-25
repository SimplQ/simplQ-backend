package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;
import java.util.ArrayList;
import java.util.List;

public class QueueDetailsResponse {
  public static class User {
    String name;
    String contactNo;
    String tokenId;

    public UserStatus getUserStatus() {
      return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
      this.userStatus = userStatus;
    }

    UserStatus userStatus;

    public String getTokenId() {
      return tokenId;
    }

    public void setTokenId(String tokenId) {
      this.tokenId = tokenId;
    }

    public User(String name, String contactNo, String tokenId, UserStatus userStatus) {
      this.name = name;
      this.contactNo = contactNo;
      this.tokenId = tokenId;
      this.userStatus = userStatus;
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
    this.users.add(new User(user.name, user.contactNumber, user.getTokenId(), user.getStatus()));
  }
}
