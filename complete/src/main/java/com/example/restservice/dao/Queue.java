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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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

  private String userId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<User> users;

  public Queue(String queueName, String userId) {
    this.queueName = queueName;
    this.userId = userId;
  }
}
