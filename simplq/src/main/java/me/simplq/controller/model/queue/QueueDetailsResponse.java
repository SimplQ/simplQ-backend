package me.simplq.controller.model.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;

@Data
public class QueueDetailsResponse {

  @Data
  public static class Token {
    private final String name;
    private final String contactNumber;
    private final String tokenId;
    private final Integer tokenNumber;
    private final Date tokenCreationTimestamp;
    private final TokenStatus tokenStatus;
    private final Boolean notifiable;
  }

  private final String queueId;
  private final String queueName;
  final Date queueCreationTimestamp;
  private final QueueStatus status;
  private final Long maxQueueCapacity;
  private final Long slotsLeft;
  private final boolean isSelfJoinAllowed;
  private final List<Token> tokens = new ArrayList<>();

  public void addToken(me.simplq.dao.Token token) {
    this.tokens.add(
        new Token(
            token.getName(),
            token.getContactNumber(),
            token.getTokenId(),
            token.getTokenNumber(),
            token.getTokenCreationTimestamp(),
            token.getStatus(),
            token.getNotifiable()));
  }
}
