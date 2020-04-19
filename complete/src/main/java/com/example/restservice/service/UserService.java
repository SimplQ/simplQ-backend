package com.example.restservice.service;

import com.example.restservice.dao.UserDao;
import com.example.restservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User addUserToQueue(String queueId, User userInfo) {
        return userDao.addUserToQueue(queueId, userInfo);
    }
}
