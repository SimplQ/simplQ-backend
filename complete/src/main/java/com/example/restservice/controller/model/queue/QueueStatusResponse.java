package com.example.restservice.controller.model.queue;

import java.util.Date;
import lombok.Data;

@Data
public class QueueStatusResponse {

  final String queueId;
  final String queueName;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
  final Date queueCreationTimestamp;
}
