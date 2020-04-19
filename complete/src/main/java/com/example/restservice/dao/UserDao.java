package com.example.restservice.dao;

import com.example.restservice.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    public User addUserToQueue(String queueId, User userInfo) {
        User user = new User();
        user.setContactNumber("123");
        user.setId("wqd");
        user.setUserName("name");
        return user;
    }
}
