package com.example.restservice.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateQueueRequest {
  String queueName;


  @JsonProperty("isPasswordProtected")
  Boolean isPasswordProtected;



}
