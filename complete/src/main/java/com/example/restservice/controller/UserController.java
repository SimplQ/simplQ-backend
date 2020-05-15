package com.example.restservice.controller;

import com.example.restservice.model.User;
import com.example.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private User user;

  @Autowired
  private UserService userService;
    
  @PostMapping(path = "v1/queue/{queueId}", consumes = "application/json", produces = "application/json")
  public User createQueue(@PathVariable String queueId, @RequestBody User userInfo) {
    return userService.addUserToQueue(queueId, userInfo);
  }
}
