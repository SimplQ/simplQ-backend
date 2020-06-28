package com.example.restservice.controller.model.token;

import com.example.restservice.constants.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDetailResponse {

  String tokenId;
  TokenStatus tokenStatus;
  String queueName;
  long aheadCount;
}
