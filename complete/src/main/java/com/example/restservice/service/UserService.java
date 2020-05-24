package com.example.restservice.service;

import com.example.restservice.constants.UserStatusConstants;
import com.example.restservice.dao.UserDao;
import com.example.restservice.model.DeleteUserRequest;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.User;
import com.example.restservice.model.UserStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.sql.Timestamp;

@Service
public class UserService {

  @Autowired private UserDao userDao;

  public UserStatusResponse addUserToQueue(JoinQueueRequest joinQueueRequest) {
    var newUser =
        new User(
            joinQueueRequest.getName(), joinQueueRequest.getContactNumber(), UserStatusConstants.WAITING);
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
  public void deleteUserfromQueue(DeleteUserRequest deleteUserRequest){
    userDao.removeUser(deleteUserRequest.getQueueId(),deleteUserRequest.getTokenId());
  }

  public void alertUser(DeleteUserRequest alertUserRequest) {
    userDao.UpdateUserStatus(alertUserRequest.getTokenId());

  }
}
