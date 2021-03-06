package me.simplq.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.simplq.controller.model.queue.CreateQueueRequest;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.PatchQueueRequest;
import me.simplq.controller.model.queue.PatchQueueResponse;
import me.simplq.controller.model.queue.PauseQueueRequest;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
import me.simplq.controller.model.queue.UpdateQueueStatusResponse;
import me.simplq.service.QueueService;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class QueueController {

  private final QueueService queueService;

  @PostMapping(path = "/queue")
  public ResponseEntity<CreateQueueResponse> createQueue(
      @Valid @RequestBody CreateQueueRequest createQueueRequest) {
    return ResponseEntity.ok(queueService.createQueue(createQueueRequest));
  }

  @GetMapping(path = "/queues")
  public ResponseEntity<MyQueuesResponse> getMyQueues() {
    return ResponseEntity.ok(queueService.getMyQueues());
  }

  @PatchMapping(path = "/queue/{queueId}")
  public ResponseEntity<PatchQueueResponse> patchQueue(
      @Valid @RequestBody PatchQueueRequest patchRequest, @PathVariable("queueId") String queueId) {

    return ResponseEntity.ok(queueService.updateMaxQueueCapacity(queueId, patchRequest));
  }

  @GetMapping(path = "/queue/{queueId}")
  public ResponseEntity<QueueDetailsResponse> getQueueDetails(
      @PathVariable("queueId") String queueId) {
    return ResponseEntity.ok(queueService.getQueueDetails(queueId));
  }

  @PostMapping(path = "/queue/{queueId}")
  public ResponseEntity<UpdateQueueStatusResponse> pauseQueueRequest(
      @Valid @RequestBody PauseQueueRequest pauseQueueRequest,
      @PathVariable("queueId") String queueId) {
    return ResponseEntity.ok(queueService.pauseQueue(pauseQueueRequest, queueId));
  }

  @DeleteMapping(path = "/queue/{queueId}")
  public ResponseEntity<UpdateQueueStatusResponse> deleteQueueRequest(
      @PathVariable("queueId") String queueId) {
    return ResponseEntity.ok(queueService.deleteQueue(queueId));
  }

  @GetMapping(path = "/queue/status")
  public ResponseEntity<QueueStatusResponse> getQueueStatus(
      @RequestParam(required = false) String queueId,
      @RequestParam(required = false) String queueName) {
    if (queueId != null) {
      return ResponseEntity.ok(queueService.getQueueStatus(queueId));
    } else if (queueName != null) {
      return ResponseEntity.ok(queueService.getQueueStatusByName(queueName));
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
