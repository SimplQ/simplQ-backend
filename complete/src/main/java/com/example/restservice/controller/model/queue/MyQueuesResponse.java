package com.example.restservice.controller.model.queue;

import java.util.List;
import lombok.Data;

@Data
public class MyQueuesResponse {

  @Data
  public static class Queue {
    private final String queueId;
    private final String queueName;
  }

  private final List<Queue> queues;
}
