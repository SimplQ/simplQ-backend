package com.example.restservice.controller.model.token;

import com.example.restservice.constants.TokenStatus;
import lombok.Data;

@Data
public class TokenDetailResponse {

  final String tokenId;
  final TokenStatus tokenStatus;
  final String queueName;
  final Long aheadCount;
  final Boolean notifiable;
}
