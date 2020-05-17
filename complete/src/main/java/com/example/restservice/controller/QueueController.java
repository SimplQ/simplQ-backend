package com.example.restservice.controller;

import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueueController {

  @Autowired private QueueService queueService;

  @GetMapping(value = "/v1/queue/{queueId}")
  public QueueDetailsResponse getQueueDetails(@PathVariable String queueId) {
    return queueService.fetchQueueData(queueId);
  }

  @PostMapping(path = "v1/queue")
  public CreateQueueResponse createQueue(@RequestBody CreateQueueRequest createQueueRequest) {
    return queueService.createQueue(createQueueRequest);
  }
}
