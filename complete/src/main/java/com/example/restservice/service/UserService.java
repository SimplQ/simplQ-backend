package com.example.restservice.service;

import com.example.restservice.dao.UserDao;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.User;
import com.example.restservice.model.User.UserStatus;
import com.example.restservice.model.UserStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserDao userDao;

  public UserStatusResponse addUserToQueue(String queueId, JoinQueueRequest joinQueueRequest) {
    var newUser =
        new User(
            joinQueueRequest.getName(), joinQueueRequest.getContactNumber(), UserStatus.WAITING);
    var tokenId = userDao.addUserToQueue(queueId, newUser).getId();
    var response = new UserStatusResponse();
    response.setAheadCount(-1);
    response.setTokenId(tokenId);
    return response;
  }

  public UserStatusResponse getStatus(String userId) {
    userDao.getUser(userId);
    return null;
  }
}
