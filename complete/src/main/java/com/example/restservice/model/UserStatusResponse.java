package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;

public class UserStatusResponse {
  long aheadCount;
  String tokenId;
  UserStatus userStatus;

  public String getTokenId() {
    return tokenId;
  }

  public UserStatus getUserStatus() {
    return userStatus;
  }

  public long getAheadCount() {
    return aheadCount;
  }

  public UserStatusResponse() {}

  public UserStatusResponse(String tokenId, UserStatus userStatus, long aheadCount) {
    this.tokenId = tokenId;
    this.userStatus = userStatus;
    this.aheadCount = aheadCount;
  }
}
