package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponse {
  long aheadCount;
  String tokenId;
  UserStatus userStatus;

}
