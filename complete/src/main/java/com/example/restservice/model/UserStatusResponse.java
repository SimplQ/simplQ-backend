package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;

public class UserStatusResponse {
  long aheadCount;
  String tokenId;
  UserStatus userStatus;

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public UserStatus getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }

  public long getAheadCount() {
    return aheadCount;
  }

  public UserStatusResponse() {}

  public UserStatusResponse(int aheadCount) {
    this.aheadCount = aheadCount;
  }

  public void setAheadCount(long aheadCount) {
    this.aheadCount = aheadCount;
  }
}
