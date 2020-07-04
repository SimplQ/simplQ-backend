package com.example.restservice.dao;

import com.example.restservice.constants.TokenStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String tokenId;

  String name;
  String contactNumber;
  TokenStatus status;
  Boolean notifyable;
  String ownerId;

  @ManyToOne Queue queue;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date timestamp;

  public Token(
      String name, String contactNumber, TokenStatus status, Boolean notifyable, String ownerId) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.notifyable = notifyable;
    this.ownerId = ownerId;
    this.timestamp = new Date();
  }
}
