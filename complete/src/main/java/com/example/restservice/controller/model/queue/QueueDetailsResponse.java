package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.TokenStatus;
import com.example.restservice.dao.Token;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
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
    TokenStatus tokenStatus;
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

  public void addUser(Token token) {
    this.users.add(
        new User(
            token.getName(),
            token.getContactNumber(),
            token.getTokenId(),
            token.getTimestamp(),
            token.getStatus(),
            token.getNotifyable()));
  }
}
