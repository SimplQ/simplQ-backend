package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class QueueDetailsResponse {


  @AllArgsConstructor
  @Getter
  @Setter
  public static class User {
    private String name;
    private String contactNo;
    private String tokenId;
    private Date timestamp;
    UserStatus userStatus;
    private Boolean notifyable;






  }

  String queueId;
  String queueName;
  List<User> users;


  public QueueDetailsResponse(String queueId, String queueName) {
    this.queueId = queueId;
    this.queueName = queueName;
    this.users = new ArrayList<>();
  }

  public void addUser(com.example.restservice.dao.User user) {
    this.users.add(
        new User(
                    user.getName(),
                    user.getContactNumber(),
                    user.getTokenId(),
                    user.getTimestamp(),
                    user.getStatus(),
                    user.getNotifyable()));
  }
}
