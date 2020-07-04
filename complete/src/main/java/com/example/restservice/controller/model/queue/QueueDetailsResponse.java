package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.TokenStatus;
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
  public static class Token {

    private String name;
    private String contactNo;
    private String tokenId;
    private Date timestamp;
    TokenStatus tokenStatus;
    private Boolean notifyable;
  }

  String queueId;
  String queueName;
  List<Token> tokens;

  public QueueDetailsResponse(String queueId, String queueName) {
    this.queueId = queueId;
    this.queueName = queueName;
    this.tokens = new ArrayList<>();
  }

  public void addToken(com.example.restservice.dao.Token token) {
    this.tokens.add(
        new Token(
            token.getName(),
            token.getContactNumber(),
            token.getTokenId(),
            token.getTimestamp(),
            token.getStatus(),
            token.getNotifyable()));
  }
}
