package me.simplq.controller.model.queue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.Data;
import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;
import me.simplq.dao.Queue;

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
  private final List<Token> removedTokens = new ArrayList<>();

  public void addToken(me.simplq.dao.Token token) {
    Token newToken =
        new Token(
            token.getName(),
            token.getContactNumber(),
            token.getTokenId(),
            token.getTokenNumber(),
            token.getTokenCreationTimestamp(),
            token.getStatus(),
            token.getNotifiable());

    if (TokenStatus.REMOVED.equals(token.getStatus())) {
      this.removedTokens.add(newToken);
    } else {
      this.tokens.add(newToken);
    }
  }

  public static QueueDetailsResponse fromEntity(Queue queue) {
    var response =
        new QueueDetailsResponse(
            queue.getQueueId(),
            queue.getQueueName(),
            queue.getQueueCreationTimestamp(),
            queue.getStatus(),
            queue.getMaxQueueCapacity(),
            queue.getSlotsLeft(),
            queue.isSelfJoinAllowed());
    queue.getTokens().stream()
        .sorted(Comparator.comparing(me.simplq.dao.Token::getTokenCreationTimestamp))
        .forEach(response::addToken);
    return response;
  }
}
