package me.simplq.controller.model.token;

import lombok.Data;
import me.simplq.constants.TokenStatus;

@Data
public class TokenDeleteResponse {
  private final String tokenId;
  private final String queueName;
  private final TokenStatus tokenStatus;
}
