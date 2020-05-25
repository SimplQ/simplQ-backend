package com.example.restservice.model;

import com.example.restservice.constants.UserStatus;

public class UserStatusResponse {
  long aheadCount;
  String tokenId;
  UserStatus status;

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
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
