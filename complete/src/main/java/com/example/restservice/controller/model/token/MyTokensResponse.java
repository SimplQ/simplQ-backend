package com.example.restservice.controller.model.token;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyTokensResponse {

  @Getter
  @AllArgsConstructor
  public static class Token {

    private final String queueName;
    private final String tokenId;
  }

  List<Token> tokens;
}
