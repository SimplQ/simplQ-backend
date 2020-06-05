package com.example.restservice.controller;

import com.example.restservice.model.DeleteUserRequest;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.UserStatusRequest;
import com.example.restservice.model.UserStatusResponse;
import com.example.restservice.service.QueueService;
import com.example.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
  @Autowired private UserService userService;
  @Autowired private QueueService queueService; // TODO move to queue controller

  @PostMapping(path = "v1/user/status")
  public UserStatusResponse getUserStatus(@RequestBody UserStatusRequest userStatusRequest) {
    return userService.getStatus(userStatusRequest.getTokenId());
  }

  @PostMapping(path = "v1/user/add")
  public UserStatusResponse joinQueue(@RequestBody JoinQueueRequest joinQueueRequest) {
    return queueService.joinQueue(joinQueueRequest);
  }

  @PostMapping(path = "v1/user/delete")
  public void deleteUser(@RequestBody DeleteUserRequest deleteUserRequest) {
    userService.deleteUserFromQueue(deleteUserRequest);
  }

  @PostMapping(path = "v1/user/alert")
  public void notifyUser(@RequestBody UserStatusRequest userStatusRequest) {
    userService.alertUser(userStatusRequest);
  }
}
