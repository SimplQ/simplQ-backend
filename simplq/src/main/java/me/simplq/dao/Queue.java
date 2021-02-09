package me.simplq.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.simplq.constants.QueueStatus;
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

  @Column(updatable = true)
  private int maxQueueCapacity;

  private QueueStatus status;

  @ManyToOne private Owner owner;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "queue")
  private List<Token> tokens;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  Date queueCreationTimestamp;

  public Queue(String queueName, Owner owner, QueueStatus status) {
    this.queueName = queueName;
    this.owner = owner;
    this.queueCreationTimestamp = new Date();
    this.status = status;
  }
}
