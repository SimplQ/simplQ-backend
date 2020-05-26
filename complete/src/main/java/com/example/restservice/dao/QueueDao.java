package com.example.restservice.dao;

import com.example.restservice.model.Queue;
import org.springframework.stereotype.Repository;

@Repository
public class QueueDao extends DaoBase {

  public Queue createQueue(String queueName) {
    var newQueue = new Queue(queueName);
    var entityManager = entityManagerFactory.createEntityManager();

    try {
      entityManager.getTransaction().begin();
      entityManager.persist(newQueue);
      entityManager.getTransaction().commit();
      return newQueue;
    } finally {
      entityManager.close();
    }
  }

  public Queue getQueue(String queueId) {
    var entityManager = entityManagerFactory.createEntityManager();
    try {
      var queue = entityManager.find(Queue.class, queueId);
      queue.getUsers().size(); // prefetch users
      if (queue == null) {
        throw new RuntimeException("Queue does not exist");
      }
      return queue;
    } finally {
      entityManager.close();
    }
  }
}
