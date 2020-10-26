package me.simplq.controller.model.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MyQueuesResponse {

  public MyQueuesResponse() {
    this.queues = new ArrayList<>();
  }

  @Data
  public static class Queue {
    private final String queueId;
    private final String queueName;
    private final Date queueCreationTimestamp;
  }

  private final List<Queue> queues;
}
