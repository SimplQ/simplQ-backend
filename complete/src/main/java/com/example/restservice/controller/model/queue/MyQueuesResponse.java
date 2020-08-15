package com.example.restservice.controller.model.queue;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class MyQueuesResponse {

  @Data
  public static class Queue {
    private final String queueId;
    private final String queueName;
    private final Date queueCreationTimestamp;
  }

  private final List<Queue> queues;
}
