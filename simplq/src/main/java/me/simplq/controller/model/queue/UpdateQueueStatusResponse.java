package me.simplq.controller.model.queue;

import lombok.Data;
import me.simplq.constants.QueueStatus;

@Data
public class UpdateQueueStatusResponse {
  public final String queueId;
  public final String queueName;
  public final QueueStatus status;
}
