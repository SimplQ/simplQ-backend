package com.example.restservice.controller.model.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QueueStatusResponse {

  final String queueId;
  final String queueName;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
}
