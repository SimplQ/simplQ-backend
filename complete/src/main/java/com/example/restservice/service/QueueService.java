package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.controller.advices.LoggedInUserInfo;
import com.example.restservice.dao.Queue;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.User;
import com.example.restservice.dao.UserRepository;
import com.example.restservice.exceptions.SQAccessDeniedException;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.MyQueuesResponse;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.model.QueueStatusResponse;
import com.example.restservice.model.UserStatusResponse;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  @Autowired
  private QueueRepository queueRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService; // TODO remove

  @Autowired
  private LoggedInUserInfo loggedInUserInfo;

  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    try {
      var queue = queueRepository
          .save(new Queue(createQueueRequest.getQueueName(), loggedInUserInfo.getUserId()));
      return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to create queue: ", e);
    }
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
                          joinQueueRequest.getNotifyable(),
                          loggedInUserInfo.getUserId());
                  newUser.setQueue(queue);
                  userRepository.save(newUser);
                  return newUser;
                })
            .orElseThrow(SQInvalidRequestException::queueNotFoundException);
    return new UserStatusResponse(
        user.getTokenId(),
        user.getStatus(),
        userService.getAheadCount(user.getTokenId()).orElseThrow(SQInternalServerException::new));
  }

  public QueueDetailsResponse getQueueDetails(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(
            queue -> {
              if (!queue.getOwnerId().equals(loggedInUserInfo.getUserId())) {
                throw new SQAccessDeniedException("You do not have access to this queue");
              }
              var resp = new QueueDetailsResponse(queueId, queue.getQueueName());
              queue.getUsers().stream()
                  .filter(user -> user.getStatus() != UserStatus.REMOVED)
                  .sorted(Comparator.comparing(User::getTimestamp))
                  .forEach(resp::addUser);
              return resp;
            })
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  public QueueStatusResponse getQueueStatus(String queueId) {
    return queueRepository.findById(queueId)
        .map(queue -> new QueueStatusResponse(queueId, queue.getQueueName(),
            queue.getUsers().stream().filter(user -> user.getStatus().equals(UserStatus.WAITING))
                .count(), Long.valueOf(queue.getUsers().size())))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(queueRepository.findByOwnerId(loggedInUserInfo.getUserId())
        .map(queue -> new MyQueuesResponse.Queue(queue.getQueueId(), queue.getQueueName())).collect(
            Collectors.toList()));
  }
}
