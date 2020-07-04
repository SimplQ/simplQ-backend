package com.example.restservice.controller.model.token;

import com.example.restservice.constants.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDeleteResponse {

  private final String queueName;
  private final TokenStatus tokenStatus;
}
