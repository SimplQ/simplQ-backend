package me.simplq.controller.model.token;

import java.util.Date;
import lombok.Data;
import me.simplq.constants.TokenStatus;

@Data
public class TokenDetailResponse {

  final String tokenId;
  final Integer tokenNumber;
  final TokenStatus tokenStatus;
  final String queueName;
  final String queueId;
  final Long aheadCount;
  final Boolean notifiable;
  final Date tokenCreationTimestamp;
}
