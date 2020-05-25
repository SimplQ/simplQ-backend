package com.example.restservice.service;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.dao.QueueDao;
import com.example.restservice.dao.UserDao;
import com.example.restservice.model.*;
import com.example.restservice.service.smsService.SmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.sql.Timestamp;

@Service
public class UserService {

  @Autowired private UserDao userDao;
  @Autowired private QueueDao queueDao;

  public UserStatusResponse addUserToQueue(JoinQueueRequest joinQueueRequest) {
    var newUser =
        new User(
            joinQueueRequest.getName(),
            joinQueueRequest.getContactNumber(),
            UserStatusConstants.WAITING);
    var tokenId = userDao.addUserToQueue(joinQueueRequest.getQueueId(), newUser).getId();
    var response = new UserStatusResponse();
    response.setAheadCount(userDao.getAheadCount(tokenId));
    response.setStatus(newUser.getStatus());
    response.setTokenId(tokenId);
    return response;
  }

  public UserStatusResponse getStatus(String userId) {
    var userStatusResponse = new UserStatusResponse();
    var user = userDao.getUser(userId);
    var timestamp = user.getTimestamp();
    userStatusResponse.setStatus(user.getStatus());
    userStatusResponse.setAheadCount(userDao.getAheadCount(userId));
    return userStatusResponse;
  }

  public void deleteUserfromQueue(DeleteUserRequest deleteUserRequest) {
    userDao.removeUser(deleteUserRequest.getQueueId(), deleteUserRequest.getTokenId());
  }

  /** Notify user on User page. Send SMS notification */
  public void alertUser(UserStatusRequest userStatusRequest) {
    userDao.UpdateUserStatus(userStatusRequest.getTokenId());

    // send SMS notification
    var user = userDao.getUser(userStatusRequest.getTokenId());
    var queue = queueDao.getQueue(userStatusRequest.getQueueId());
    SmsManager.notify(user.getContactNumber(), queue.getQueueName());
  }
}
