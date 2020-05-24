package com.example.restservice.dao;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.model.User;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
public class UserDao extends DaoBase {

  @Autowired QueueDao queueDao;

  public User addUserToQueue(String queueId, User user) {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    var queue = queueDao.getQueue(queueId);
    user.setQueue(queue);
    entityManager.persist(user);
    entityManager.getTransaction().commit();
    entityManager.close();

    return user;
  }

  public List<User> getUsersInQueue(String queueId) {
    return queueDao.getQueue(queueId).getUsers();
  }

  public User getUser(String userId) {
    var entityManager = entityManagerFactory.createEntityManager();
    return entityManager.find(User.class, userId);
  }

    public void removeUser(String queueId, String tokenId) {
    var entityManager= entityManagerFactory.createEntityManager();
     var user =entityManager.find(User.class, tokenId);
     entityManager.remove(user);
    }

  public void UpdateUserStatus(String tokenId) {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    var user = entityManager.find(User.class, tokenId);
    user.setStatus(UserStatusConstants.NOTIFIED);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  public int getAheadCount(String userId) {
    var entityManager = entityManagerFactory.createEntityManager();
    var user = entityManager.find(User.class,userId);

    Query query = entityManager.createNativeQuery("select count(*) from User u where :timestamp > u.timestamp and u.queue = :queue");
    query.setParameter("timestamp",user.getTimestamp());
    query.setParameter("queue",user.getQueue());
    return query.getFirstResult();
  }
}
