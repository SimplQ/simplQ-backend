package com.example.restservice.controller.model.queue;

import com.example.restservice.dao.CustomQuestions;
import lombok.Data;

import java.util.Date;

@Data
public class QueueStatusResponse {

  final String queueId;
  final String queueName;

  final Long numberOfActiveTokens;
  final Long totalNumberOfTokens;
  final Date queueCreationTimestamp;
  final CustomQuestions customQuestions;
}
