package com.example.restservice.controller.model.queue;

import lombok.Data;

@Data
public class QueueStatusResponse {

  final String queueId;
  final String queueName;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
}
