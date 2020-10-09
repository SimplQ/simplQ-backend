package me.simplq.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import me.simplq.constants.QueueStatus;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

  private QueueStatus status;
  private String ownerId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<Token> tokens;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date queueCreationTimestamp;

  public Queue(String queueName, String ownerId, QueueStatus status) {
    this.queueName = queueName;
    this.ownerId = ownerId;
    this.queueCreationTimestamp = new Date();
    this.status = status;
  }
}
