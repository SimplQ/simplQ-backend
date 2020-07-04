package com.example.restservice.controller.model.queue;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyQueuesResponse {

  @Getter
  @AllArgsConstructor
  public static class Queue {

    private final String queueId;
    private final String queueName;
  }

  private final List<Queue> queues;
}
