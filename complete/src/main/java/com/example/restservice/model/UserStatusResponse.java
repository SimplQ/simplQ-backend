package com.example.restservice.model;

import com.example.restservice.constants.UserStatusConstants;

public class UserStatusResponse {
  int aheadCount;
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




  public int getAheadCount() {
    return aheadCount;
  }

  public UserStatusResponse() {}

  public UserStatusResponse(int aheadCount) {
    this.aheadCount = aheadCount;
  }

  public void setAheadCount(int aheadCount) {
    this.aheadCount = aheadCount;
  }
}
