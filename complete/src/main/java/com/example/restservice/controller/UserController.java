package com.example.restservice.controller;

import com.example.restservice.dao.QueueDAO;
import com.example.restservice.model.Queue;
import com.example.restservice.model.User;
import com.example.restservice.service.QueueService;
import com.example.restservice.service.UserService;
import dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
