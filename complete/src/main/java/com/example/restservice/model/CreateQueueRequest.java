package com.example.restservice.model;

public class CreateQueueRequest {
  String queueName;

  public CreateQueueRequest(String queueName) {
    this.queueName = queueName;
  }

  public CreateQueueRequest() {}

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }
}
