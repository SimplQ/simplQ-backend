package com.example.restservice.dao;

import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "queue")
public class Queue {

  @Column(unique=true)
  private String queueName;

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String queueId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<User> users;

  public List<User> getUsers() {
    return users;
  }

  protected Queue() {}

  public String getQueueName() {
    return queueName;
  }

  public Queue(String queueName) {
    this.queueName = queueName;
  }

  public String getQueueId() {
    return queueId;
  }
}
