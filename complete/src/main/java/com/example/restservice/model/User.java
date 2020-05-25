package com.example.restservice.model;

import javax.persistence.*;

import com.example.restservice.constants.UserStatusConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.Date;

@Entity
public class User {
  public User() {
  }

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String tokenId;

  String name;
  String contactNumber;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  Date timestamp;

  public Queue getQueue() {
    return queue;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  @ManyToOne Queue queue;

  public User(String name, String contactNumber, UserStatusConstants status) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
  }

  UserStatusConstants status;

  public UserStatusConstants getStatus() {
    return status;
  }

  public void setStatus(UserStatusConstants status) {
    this.status = status;
  }

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

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }
}
