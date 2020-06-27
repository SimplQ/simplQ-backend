package com.example.restservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequest {
  String tokenId;
  String queueId;
}
