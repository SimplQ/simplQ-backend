package com.example.restservice.controller.model.queue;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class CreateQueueRequest {

  @Pattern(regexp = "^[a-zA-Z0-9-]+$")
  String queueName;
}
