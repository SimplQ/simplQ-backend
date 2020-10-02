package me.simplq.controller.model.token;

import me.simplq.constants.TokenStatus;
import lombok.Data;

@Data
public class TokenNotifyResponse {

  private final String tokenId;
  private final TokenStatus tokenStatus;
}
