package com.example.restservice.service;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.dao.QueueDao;
import com.example.restservice.dao.UserDao;
import com.example.restservice.model.DeleteUserRequest;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.User;
import com.example.restservice.model.UserStatusRequest;
import com.example.restservice.model.UserStatusResponse;
import com.example.restservice.service.smsService.SmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    var tokenId = userDao.addUserToQueue(joinQueueRequest.getQueueId(), newUser).getTokenId();
    var response = new UserStatusResponse();
    userDao
        .getAheadCount(tokenId)
        .ifPresentOrElse(
            response::setAheadCount,
            () -> {
              throw new IllegalStateException();
            });
    response.setStatus(newUser.getStatus());
    response.setTokenId(tokenId);
    return response;
  }

  public UserStatusResponse getStatus(String tokenId) {
    var userStatusResponse = new UserStatusResponse();
    var user = userDao.getUser(tokenId);
    userStatusResponse.setStatus(user.getStatus());
    userDao.getAheadCount(tokenId).ifPresent(userStatusResponse::setAheadCount);
    userStatusResponse.setTokenId(tokenId);
    return userStatusResponse;
  }

  public void deleteUserFromQueue(DeleteUserRequest deleteUserRequest) {
    userDao.UpdateUserStatus(deleteUserRequest.getTokenId(), UserStatusConstants.REMOVED);
  }

  /** Notify user on User page. Send SMS notification */
  public void alertUser(UserStatusRequest userStatusRequest) {
    // send SMS notification
    var user = userDao.getUser(userStatusRequest.getTokenId());
    SmsManager.notify(user.getContactNumber(), user.getQueue().getQueueName());

    userDao.UpdateUserStatus(userStatusRequest.getTokenId(), UserStatusConstants.NOTIFIED);
  }
}
