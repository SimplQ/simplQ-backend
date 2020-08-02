package com.example.restservice.service;

import com.example.restservice.constants.QueueStatus;
import com.example.restservice.constants.TokenStatus;
import com.example.restservice.controller.advices.LoggedInUserInfo;
import com.example.restservice.controller.model.queue.*;
import com.example.restservice.dao.Queue;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.Token;
import com.example.restservice.exceptions.SQAccessDeniedException;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

  private final QueueRepository queueRepository;
  private final LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    try {
      var queue =
          queueRepository.save(
              new Queue(createQueueRequest.getQueueName(), loggedInUserInfo.getUserId(), QueueStatus.ACTIVE));
      return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to create queue: ", e);
    }
  }

  @Transactional
  public PauseQueueResponse pauseQueue(PauseQueueRequest pauseQueueRequest, String queueId) {
    try {
      var queue =
          queueRepository
          .findById(queueId)
          .map(
              queue1 -> {
                if (!queue1.getOwnerId().equals(loggedInUserInfo.getUserId())) {
                  throw new SQAccessDeniedException("You do not have access to this queue");
                }
                queue1.setStatus(pauseQueueRequest.getStatus());
                return queueRepository.save(queue1);
              }
          ).get();
      return new PauseQueueResponse(queue.getQueueId(), queue.getQueueName(), queue.getStatus());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to update queue: ", e);
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
                  .filter(token -> token.getStatus() != TokenStatus.REMOVED)
                  .sorted(Comparator.comparing(Token::getTimestamp))
                  .forEach(resp::addToken);
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

  @Transactional
  public QueueStatusResponse getQueueStatusByName(String queueName) {
    return queueRepository
        .findByQueueName(queueName)
        .map(
            queue ->
                new QueueStatusResponse(
                    queue.getQueueId(),
                    queue.getQueueName(),
                    queue.getTokens().stream()
                        .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                        .count(),
                    Long.valueOf(queue.getTokens().size())))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }
}
