package com.example.restservice.model;

import com.example.restservice.constants.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenNotifyResponse {

  private final String tokenId;
  private final TokenStatus tokenStatus;
}
