package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.TokenStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class QueueDetailsResponse {

  @Data
  public static class Token {
    private final String name;
    private final String contactNo;
    private final String tokenId;
    private final Integer tokenNumber;
    private final Date timestamp;
    private final TokenStatus tokenStatus;
    private final Boolean notifiable;
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
            token.getTokenNumber(),
            token.getTimestamp(),
            token.getStatus(),
            token.getNotifiable()));
  }
}
