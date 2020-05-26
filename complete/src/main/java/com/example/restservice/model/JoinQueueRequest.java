package com.example.restservice.model;

public class JoinQueueRequest {
  private String name;
  private String contactNumber;
  private String queueId;
  private Boolean notifyable; // TODO(Spelling)

  public Boolean getNotifyable() {
    return notifyable;
  }

  public void setNotifyable(Boolean notifyable) {
    this.notifyable = notifyable;
  }

  public String getQueueId() {
    return queueId;
  }

  public void setQueueId(String queueId) {
    this.queueId = queueId;
  }

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
