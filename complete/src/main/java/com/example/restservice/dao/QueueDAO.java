package com.example.restservice.dao;

import com.example.restservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class QueueDAO {




    public List<User> fetchQueueData(String queueId){
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setUserName("Navaneeth");
        user.setContactNumber("0586054i0");
        userList.add(user);
        return userList;
    }


}
