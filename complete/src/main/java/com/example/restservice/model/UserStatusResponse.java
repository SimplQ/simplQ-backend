package com.example.restservice.model;

public class UserStatusResponse {
  int aheadCount;
  String tokenId;

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
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
