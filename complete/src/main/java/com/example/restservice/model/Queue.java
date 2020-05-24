package com.example.restservice.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Queue")
public class Queue {

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

  public Queue() {}

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public String getQueueName() {
    return queueName;
  }

  public Queue(String queueName) {
    this.queueName = queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public String getQueueId() {
    return queueId;
  }

  public void setQueueId(String queueId) {
    this.queueId = queueId;
  }

  public void addUser(User user) {
    users.add(user);
  }
}
