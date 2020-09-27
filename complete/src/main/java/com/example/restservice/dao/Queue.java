package com.example.restservice.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "queue")
@Getter
@Setter
@NoArgsConstructor
public class Queue {

  @Column(unique = true)
  private String queueName;

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String queueId;

  private String ownerId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<Token> tokens;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date queueCreationTimestamp;

  @Column(length = 100000)
  private String customQuestions;

  public Queue(String queueName, String ownerId) {
    this.queueName = queueName;
    this.ownerId = ownerId;
    this.queueCreationTimestamp = new Date();
  }
}
