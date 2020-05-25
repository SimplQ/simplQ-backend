package com.example.restservice.dao;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.model.User;
import java.math.BigInteger;
import java.util.List;

import java.util.Optional;
import javax.swing.text.html.Option;
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

  public User getUser(String tokenId) {
    var entityManager = entityManagerFactory.createEntityManager();
    return entityManager.find(User.class, tokenId);
  }

    public void removeUser(String queueId, String tokenId) {
    var entityManager= entityManagerFactory.createEntityManager();
     var user = entityManager.find(User.class, tokenId);
     entityManager.remove(user);
    }

  public void UpdateUserStatus(String tokenId, UserStatusConstants status) {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    var user = entityManager.find(User.class, tokenId);
    user.setStatus(status);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  public Optional<Long> getAheadCount(String tokenId) {
    var entityManager = entityManagerFactory.createEntityManager();
    var user = entityManager.find(User.class,tokenId);

    if (user.getStatus() == UserStatusConstants.REMOVED) {
      return Optional.empty();
    }

    return Optional.of(user.getQueue().getUsers().stream().filter(fellowUser ->
        fellowUser.getTimestamp().before(user.getTimestamp())
        && !fellowUser.getStatus().equals(UserStatusConstants.REMOVED)
    ).count());
  }
}
