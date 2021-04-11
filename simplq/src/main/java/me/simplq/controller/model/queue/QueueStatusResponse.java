package me.simplq.controller.model.queue;

import java.util.Date;
import lombok.Data;
import me.simplq.constants.QueueStatus;

@Data
public class QueueStatusResponse {

  final String queueId;
  final String queueName;
  final QueueStatus status;
  final Long maxQueueCapacity;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
  final Long slotsLeft;
  final Date queueCreationTimestamp;
}
