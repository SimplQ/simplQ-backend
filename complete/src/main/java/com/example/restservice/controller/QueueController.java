package com.example.restservice.controller;

import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.QueueDetailsRequest;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class QueueController {

  @Autowired
  private QueueService queueService;

  @PostMapping(path = "/queue/info")
  public ResponseEntity<QueueDetailsResponse> getQueueDetails(
      @RequestBody QueueDetailsRequest queueDetailsRequest) {
    return ResponseEntity.ok(queueService.fetchQueueData(queueDetailsRequest.getQueueId()));
  }

  @PostMapping(path = "/queue/create")
  public ResponseEntity<CreateQueueResponse> createQueue(
      @RequestBody CreateQueueRequest createQueueRequest) {
    return ResponseEntity.ok(queueService.createQueue(createQueueRequest));
  }
}
