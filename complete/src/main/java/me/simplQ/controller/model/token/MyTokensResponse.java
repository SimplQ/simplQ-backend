package me.simplq.controller.model.token;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class MyTokensResponse {

  @Data
  public static class Token {
    private final String queueName;
    private final String tokenId;
    private final Date tokenCreationTimestamp;
  }

  final List<Token> tokens;
}
