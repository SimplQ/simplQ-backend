package com.example.restservice.model;

import lombok.Data;

@Data
public class CreateTokenRequest {

  private String name;
  private String contactNumber;
  private String queueId;
  private Boolean notifyable; // TODO(Spelling)
}
