package me.simplq.controller.model.queue;

import lombok.Data;
import me.simplq.constants.QueueStatus;
import me.simplq.dao.Queue;

@Data
public class UpdateQueueStatusResponse {

  public final String queueId;
  public final String queueName;
  public final QueueStatus status;

  public static UpdateQueueStatusResponse fromEntity(Queue queue) {
    return new UpdateQueueStatusResponse(
        queue.getQueueId(), queue.getQueueName(), queue.getStatus());
  }
}
