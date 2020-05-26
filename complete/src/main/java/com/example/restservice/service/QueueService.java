package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.dao.QueueDao;
import com.example.restservice.dao.UserDao;
import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.model.User;
import java.util.Comparator;
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
    var queue = queueDAO.getQueue(queueId);
    var resp = new QueueDetailsResponse(queueId, queue.getQueueName());
    queue.getUsers().stream()
        .filter(user -> user.getStatus() != UserStatus.REMOVED)
        .sorted(Comparator.comparing(User::getTimestamp))
        .forEach(resp::addUser);
    return resp;
  }
}
