package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.dao.Queue;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.User;
import com.example.restservice.dao.UserRepository;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import com.example.restservice.model.CreateQueueRequest;
import com.example.restservice.model.CreateQueueResponse;
import com.example.restservice.model.JoinQueueRequest;
import com.example.restservice.model.QueueDetailsResponse;
import com.example.restservice.model.UserStatusResponse;
import java.util.Comparator;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  @Autowired private QueueRepository queueRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserService userService; // TODO remove

  private Random randomNumber = new Random();
  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    try {


      Queue queueDao;
      // if isPasswordProtected is true, generate a 4 digit pin.
      if(Boolean.TRUE.equals(createQueueRequest.getIsPasswordProtected())) {
        String queuePin=String.format("%04d", randomNumber.nextInt(10000));
        queueDao=new Queue(createQueueRequest.getQueueName(),createQueueRequest.getIsPasswordProtected(),queuePin);
        var queue = queueRepository.save(queueDao);
        return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId(),queue.getQueuePassword());
      }
      else{
        queueDao =new Queue(createQueueRequest.getQueueName());
        var queue = queueRepository.save(queueDao);
        return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
      }



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
                  // if Pin is incorrect,no access to queue.
                  if (queue.isPasswordProtected()
                      && (!queue.getQueuePassword().equals(joinQueueRequest.getQueuePassword()))) {
                    throw SQInvalidRequestException.queuePasswordIncorrectException();
                  }

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
            .orElseThrow(SQInvalidRequestException::queueNotFoundException);
    return new UserStatusResponse(
        user.getTokenId(),
        user.getStatus(),
        userService.getAheadCount(user.getTokenId()).orElseThrow(SQInternalServerException::new));
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
              resp.setIsPasswordProtected(queue.isPasswordProtected());
              return resp;
            })
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }



}