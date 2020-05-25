package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.dao.QueueDao;
import com.example.restservice.dao.UserDao;
import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.QueueDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  @Autowired private QueueDao queueDAO;
  @Autowired private UserDao userDao;

  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    var queue = queueDAO.createQueue(createQueueRequest.getQueueName());
    return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
  }

  public QueueDetailsResponse fetchQueueData(String queueId) {
    var resp = new QueueDetailsResponse(queueId);
    userDao.getUsersInQueue(queueId).stream()
        .filter(user -> user.getStatus() != UserStatus.REMOVED)
        .forEach(resp::addUser);
    return resp;
  }
}
