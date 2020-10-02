package me.simplq.controller.model.token;

import lombok.Data;

@Data
public class CreateTokenRequest {

  private String name;
  private String contactNumber;
  private String queueId;
  private Boolean notifiable;
}
