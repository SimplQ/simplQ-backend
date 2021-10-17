package me.simplq.controller.model.queue;

import java.util.Date;
import lombok.Data;
import me.simplq.constants.QueueStatus;
import me.simplq.dao.Queue;

@Data
public class QueueStatusResponse {

  final String queueId;
  final String queueName;
  final QueueStatus status;
  final Long maxQueueCapacity;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
  final Long slotsLeft;
  final Integer lastRemovedTokenNumber;
  final Date queueCreationTimestamp;
  final boolean isSelfJoinAllowed;
  final boolean notifyByEmail;

  public static QueueStatusResponse fromEntity(Queue queue) {
    return new QueueStatusResponse(
        queue.getQueueId(),
        queue.getQueueName(),
        queue.getStatus(),
        queue.getMaxQueueCapacity(),
        queue.getActiveTokensCount(),
        queue.getTotalTokensCount(),
        queue.getSlotsLeft(),
        queue.getLastRemovedTokenNumber(),
        queue.getQueueCreationTimestamp(),
        queue.isSelfJoinAllowed(),
        queue.isNotifyByEmail());
  }
}
