package com.example.restservice.controller;

import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.UserStatusResponse;
import com.example.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  @Autowired private UserService userService;

  @GetMapping(path = "v1/{userId}")
  public UserStatusResponse getUserStatus(@PathVariable String userId) {
    return userService.getStatus(userId);
  }

  @PostMapping(path = "v1/{userId}")
  public UserStatusResponse addUser(@PathVariable String userId) {
    return userService.getStatus(userId);
  }

  @PostMapping(path = "v1/join/{queueId}")
  public UserStatusResponse joinQueue(
      @RequestBody JoinQueueRequest joinQueueRequest, @PathVariable String queueId) {
    return userService.addUserToQueue(queueId, joinQueueRequest);
  }
}
