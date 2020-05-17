package com.example.restservice.model;

public class CreateQueueResponse {
  public final String queueName;
  public final String queueId;

  public CreateQueueResponse(String queueName, String queueId) {
    this.queueName = queueName;
    this.queueId = queueId;
  }
}
