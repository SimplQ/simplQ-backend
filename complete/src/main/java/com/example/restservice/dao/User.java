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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "QueueUser") // As user is a reserved keyword in PostgreSQL
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String tokenId;

  String name;
  String contactNumber;
  UserStatus status;
  Boolean notifyable;
  String userId;

  @ManyToOne
  Queue queue;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  Date timestamp;

  public User(String name, String contactNumber, UserStatus status, Boolean notifyable,
      String userId) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.notifyable = notifyable;
    this.userId = userId;
  }

}
