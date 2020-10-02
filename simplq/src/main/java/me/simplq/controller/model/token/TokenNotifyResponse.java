package me.simplq.controller.model.token;

import lombok.Data;
import me.simplq.constants.TokenStatus;

@Data
public class TokenNotifyResponse {

  private final String tokenId;
  private final TokenStatus tokenStatus;
}
