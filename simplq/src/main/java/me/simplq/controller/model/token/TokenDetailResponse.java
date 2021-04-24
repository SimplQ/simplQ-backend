package me.simplq.controller.model.token;

import java.util.Date;
import lombok.Data;
import me.simplq.constants.TokenStatus;
import me.simplq.dao.Token;

@Data
public class TokenDetailResponse {

  final String tokenId;
  final String name;
  final String contactNumber;
  final Integer tokenNumber;
  final TokenStatus tokenStatus;
  final String queueName;
  final String queueId;
  final Long aheadCount;
  final Boolean notifiable;
  final Date tokenCreationTimestamp;

  public static TokenDetailResponse fromEntity(Token token) {
    return new TokenDetailResponse(
        token.getTokenId(),
        token.getName(),
        token.getContactNumber(),
        token.getTokenNumber(),
        token.getStatus(),
        token.getQueue().getQueueName(),
        token.getQueue().getQueueId(),
        token.getAheadCount(),
        token.getNotifiable(),
        token.getTokenCreationTimestamp());
  }
}
