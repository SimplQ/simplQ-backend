package com.example.restservice.model;

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
  long aheadCount;
}
