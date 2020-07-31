package com.example.restservice.controller.model.token;

import java.util.List;
import lombok.Data;

@Data
public class MyTokensResponse {

  @Data
  public static class Token {
    private final String queueName;
    private final String tokenId;
  }

  final List<Token> tokens;
}
