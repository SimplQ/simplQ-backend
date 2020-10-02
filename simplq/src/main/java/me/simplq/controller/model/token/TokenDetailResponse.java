package me.simplq.controller.model.token;

import me.simplq.constants.TokenStatus;
import java.util.Date;
import lombok.Data;

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
