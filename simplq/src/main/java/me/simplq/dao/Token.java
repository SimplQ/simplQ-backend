package me.simplq.dao;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.simplq.constants.TokenStatus;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

  private static final String URL_PREFIX = "https://simplq.me/token/";

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

  public String getTokenUrl() {
    return URL_PREFIX + tokenId;
  }
}
