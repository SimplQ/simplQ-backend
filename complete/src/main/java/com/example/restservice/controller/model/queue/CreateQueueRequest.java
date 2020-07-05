package com.example.restservice.controller.model.queue;

import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateQueueRequest {

  @Pattern(regexp = "^[a-zA-Z0-9-]+$")
  String queueName;
}
