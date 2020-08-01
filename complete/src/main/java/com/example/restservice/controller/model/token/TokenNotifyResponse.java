package com.example.restservice.controller.model.token;

import com.example.restservice.constants.TokenStatus;
import lombok.Data;

@Data
public class TokenNotifyResponse {

  private final String tokenId;
  private final TokenStatus tokenStatus;
}
