package me.simplq.controller.model.queue;

import lombok.Value;

@Value
public class PatchQueueRequest {

  private Integer maxQueueCapacity;
  private Boolean isSelfJoinAllowed;
  private Boolean notifyByEmail;
}
