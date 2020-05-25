package com.example.restservice.model;

import com.example.restservice.constants.UserStatusConstants;

public class UserStatusResponse {
  long aheadCount;
  String tokenId;
  UserStatusConstants status;

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public UserStatusConstants getStatus() {
    return status;
  }

  public void setStatus(UserStatusConstants status) {
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
