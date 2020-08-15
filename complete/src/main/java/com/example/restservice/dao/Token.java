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

  Integer tokenNumber;
  String name;
  String contactNumber;
  TokenStatus status;
  Boolean notifiable;
  String ownerId;

  @ManyToOne Queue queue;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date tokenCreationTimestamp;

  public Token(
      String name, String contactNumber, TokenStatus status, Boolean notifiable, String ownerId) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.notifiable = notifiable;
    this.ownerId = ownerId;
    this.tokenCreationTimestamp = new Date();
  }
}
