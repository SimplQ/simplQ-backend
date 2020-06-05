package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.UserRepository;
import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.Queue;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.model.User;
import com.example.restservice.model.UserStatusResponse;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  @Autowired private QueueRepository queueRepository;
  @Autowired private UserRepository userRepository;

  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    var queue = queueRepository.save(new Queue(createQueueRequest.getQueueName()));
    return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
  }

  public UserStatusResponse joinQueue(JoinQueueRequest joinQueueRequest) {
    var user =
        queueRepository
            .findById(joinQueueRequest.getQueueId())
            .map(
                queue -> {
                  var newUser =
                      new User(
                          joinQueueRequest.getName(),
                          joinQueueRequest.getContactNumber(),
                          UserStatus.WAITING,
                          joinQueueRequest.getNotifyable());
                  newUser.setQueue(queue);
                  userRepository.save(newUser);
                  return newUser;
                })
            .orElseThrow(RuntimeException::new); // TODO Custom Exception
    int aheadCount = 10; // get aheadcount // todo
    return new UserStatusResponse(user.getTokenId(), user.getStatus(), aheadCount);
  }

  public QueueDetailsResponse fetchQueueData(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(
            queue -> {
              var resp = new QueueDetailsResponse(queueId, queue.getQueueName());
              queue.getUsers().stream()
                  .filter(user -> user.getStatus() != UserStatus.REMOVED)
                  .sorted(Comparator.comparing(User::getTimestamp))
                  .forEach(resp::addUser);
              return resp;
            })
        .orElseThrow(RuntimeException::new); // Todo Custom Exception
  }
}
