package me.simplq.dao;

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
import me.simplq.constants.TokenStatus;
import org.apache.commons.lang3.StringUtils;
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
  String ownerId;
  String emailId;

  @ManyToOne Queue queue;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date tokenCreationTimestamp;

  @Column(updatable = true)
  @Temporal(TemporalType.TIMESTAMP)
  Date tokenDeletionTimestamp;

  public Token(String name, String contactNumber, TokenStatus status, String ownerId) {
    this.name = name;
    this.contactNumber = contactNumber;
    this.status = status;
    this.ownerId = ownerId;
    this.tokenCreationTimestamp = new Date();
  }

  public Long getAheadCount() {
    if (this.getStatus() == TokenStatus.REMOVED) {
      return null;
    }
    return this.getQueue().getTokens().stream()
        .filter(
            fellowUser ->
                fellowUser.getTokenCreationTimestamp().before(this.getTokenCreationTimestamp())
                    && !fellowUser.getStatus().equals(TokenStatus.REMOVED))
        .count();
  }

  public void delete() {
    this.status = TokenStatus.REMOVED;
    this.tokenDeletionTimestamp = new Date();
  }

  public boolean isNotifiable() {
    return TokenStatus.REMOVED.equals(status) && StringUtils.isNotBlank(emailId);
  }
}
