package me.simplq.controller.model.token;

import me.simplq.constants.TokenStatus;
import lombok.Data;

@Data
public class TokenDeleteResponse {
  private final String queueName;
  private final TokenStatus tokenStatus;
}
