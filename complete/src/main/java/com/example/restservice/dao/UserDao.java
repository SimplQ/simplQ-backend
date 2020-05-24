package com.example.restservice.dao;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.model.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends DaoBase {

  @Autowired QueueDao queueDao;

  public User addUserToQueue(String queueId, User user) {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    var queue = queueDao.getQueue(queueId);
    entityManager.persist(user);
    queue.addUser(user);
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

}
