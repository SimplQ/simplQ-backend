package com.example.restservice.dao;

import com.example.restservice.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public class QueueDAO {

  public List<User> fetchQueueData(String queueId) {
    List<User> userList = new ArrayList<>();
    User user = new User();
    user.setUserName("Navaneeth");
    user.setContactNumber("0586054i0");
    userList.add(user);
    return userList;
  }
}
