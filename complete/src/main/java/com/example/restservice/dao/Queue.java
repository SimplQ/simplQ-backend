package com.example.restservice.dao;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "queue")
@Getter
public class Queue {

  @Column(unique=true)
  private String queueName;

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String queueId;

  private boolean isPasswordProtected;
  private String queuePassword;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<User> users;

  protected Queue() {}

  public Queue(String queueName)
  {
    this.queueName=queueName;
  }

  public Queue(String queueName, boolean isPasswordProtected, String queuePassword) {
    this.queueName = queueName;
    this.isPasswordProtected = isPasswordProtected;
    this.queuePassword = queuePassword;
  }
}