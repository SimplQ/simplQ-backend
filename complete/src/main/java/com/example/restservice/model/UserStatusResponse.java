package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponse {

  String tokenId;
  UserStatus userStatus;
  long aheadCount;

  ;
}
