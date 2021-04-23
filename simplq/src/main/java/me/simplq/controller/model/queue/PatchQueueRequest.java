package me.simplq.controller.model.queue;

import lombok.Data;

@Data
public class PatchQueueRequest {

  private Integer maxQueueCapacity;
  private Boolean isSelfJoinAllowed;
}
