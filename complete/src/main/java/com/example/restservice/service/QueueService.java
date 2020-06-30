package com.example.restservice.service;

import com.example.restservice.constants.TokenStatus;
import com.example.restservice.controller.advices.LoggedInUserInfo;
import com.example.restservice.controller.model.queue.CreateQueueRequest;
import com.example.restservice.controller.model.queue.CreateQueueResponse;
import com.example.restservice.controller.model.queue.MyQueuesResponse;
import com.example.restservice.controller.model.queue.QueueDetailsResponse;
import com.example.restservice.controller.model.queue.QueueStatusResponse;
import com.example.restservice.dao.Queue;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.Token;
import com.example.restservice.dao.TokenRepository;
import com.example.restservice.exceptions.SQAccessDeniedException;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  @Autowired private QueueRepository queueRepository;
  @Autowired private TokenRepository tokenRepository;

  @Autowired private LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    try {
      var queue =
          queueRepository.save(
              new Queue(createQueueRequest.getQueueName(), loggedInUserInfo.getUserId()));
      return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to create queue: ", e);
    }
  }

  @Transactional
  public QueueDetailsResponse getQueueDetails(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(
            queue -> {
              if (!queue.getOwnerId().equals(loggedInUserInfo.getUserId())) {
                throw new SQAccessDeniedException("You do not have access to this queue");
              }
              var resp = new QueueDetailsResponse(queueId, queue.getQueueName());
              queue.getTokens().stream()
                  .filter(user -> user.getStatus() != TokenStatus.REMOVED)
                  .sorted(Comparator.comparing(Token::getTimestamp))
                  .forEach(resp::addUser);
              return resp;
            })
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public QueueStatusResponse getQueueStatus(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(
            queue ->
                new QueueStatusResponse(
                    queueId,
                    queue.getQueueName(),
                    queue.getTokens().stream()
                        .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                        .count(),
                    Long.valueOf(queue.getTokens().size())))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(
        queueRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .map(queue -> new MyQueuesResponse.Queue(queue.getQueueId(), queue.getQueueName()))
            .collect(Collectors.toList()));
  }
}
