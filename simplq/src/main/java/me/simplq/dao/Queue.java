package me.simplq.dao;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;
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

  @Column private long maxQueueCapacity;

  private QueueStatus status;

  @ManyToOne private Owner owner;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<Token> tokens;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date queueCreationTimestamp;

  // Allow users to join the queue on their own using the queue link. If not enabled,
  // only queue owner will be able to add people to the queue. Set to true by default.
  @Column private boolean isSelfJoinAllowed;

  // Send email notifications to people in queue. Set to false by default. If enabled,
  // email address of people in queue will be collected when creating token.
  @Column private boolean notifyByEmail;

  public Queue(String queueName, Owner owner) {
    this.queueName = queueName;
    this.owner = owner;
    this.queueCreationTimestamp = new Date();
    this.status = QueueStatus.ACTIVE;
    this.maxQueueCapacity = 10000;
    this.isSelfJoinAllowed = true;
    this.notifyByEmail = true;
  }

  public long getActiveTokensCount() {
    return tokens.stream()
        .filter(token1 -> !token1.getStatus().equals(TokenStatus.REMOVED))
        .count();
  }

  public long getTotalTokensCount() {
    return tokens.size();
  }

  public boolean isFull() {
    return getSlotsLeft() <= 0;
  }

  public Long getSlotsLeft() {
    return maxQueueCapacity - getActiveTokensCount();
  }

  public Integer getLastRemovedTokenNumber() {
    return tokens.stream()
        .filter(token1 -> token1.getStatus().equals(TokenStatus.REMOVED))
        .max(Comparator.comparing(Token::getTokenDeletionTimestamp))
        .map(Token::getTokenNumber)
        .orElse(0);
  }
}
