package me.simplq.controller.model.queue;

import lombok.Data;
import me.simplq.constants.QueueStatus;

@Data
public class PauseQueueRequest {
  QueueStatus status;
}
