package com.example.restservice.dao;

import com.example.restservice.constants.UserStatus;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "QueueUser") // As user is a reserved keyword in PostgreSQL
public class User {
  protected User() {}

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String tokenId;

  String name;
  String contactNumber;
  UserStatus status;
  Boolean notifyable;

  @ManyToOne Queue queue;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  Date timestamp;

  public Queue getQueue() {
    return queue;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  public Boolean getNotifyable() {
    return notifyable;
  }

  public User(String name, String contactNumber, UserStatus status, Boolean notifyable) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.notifyable = notifyable;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public String getTokenId() {
    return tokenId;
  }

  public Date getTimestamp() {
    return timestamp;
  }
}
