package com.example.restservice.controller.model.token;

import com.example.restservice.constants.TokenStatus;
import lombok.Data;

@Data
public class TokenDeleteResponse {
  private final String queueName;
  private final TokenStatus tokenStatus;
}
