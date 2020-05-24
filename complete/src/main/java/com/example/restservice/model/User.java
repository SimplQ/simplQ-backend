package com.example.restservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.example.restservice.constants.UserStatusConstants;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Entity
public class User {
  public User() {
  }



  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id;

  String name;
  String contactNumber;



  Timestamp timestamp;

  public Queue getQueue() {
    return queue;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  @ManyToOne Queue queue;

  public User(String name, String contactNumber, UserStatusConstants status, Timestamp timestamp) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.timestamp = timestamp;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }
}
