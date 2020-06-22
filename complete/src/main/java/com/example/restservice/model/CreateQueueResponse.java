package com.example.restservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor

public class CreateQueueResponse {
  public final String queueName;
  public final String queueId;
  @JsonInclude(Include.NON_DEFAULT) //no default values required
  public  int queuePassword;



}