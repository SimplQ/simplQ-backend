package com.example.restservice.model;

public class JoinQueueRequest {
  private String name;
  private String contactNumber;

  public JoinQueueRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }
}
