package me.simplq.controller.model.queue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchQueueResponse {
  String queueId;
  String queueName;
  int maxQueueCapacity;
}
