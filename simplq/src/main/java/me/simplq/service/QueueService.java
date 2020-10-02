package me.simplq.service;

import me.simplq.constants.TokenStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.controller.model.queue.CreateQueueRequest;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
import me.simplq.dao.Queue;
import me.simplq.dao.QueueRepository;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQAccessDeniedException;
import me.simplq.exceptions.SQInvalidRequestException;
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
          queueRepository.saveAndFlush(
              new Queue(createQueueRequest.getQueueName(), loggedInUserInfo.getUserId()));
      return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
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
              var resp =
                  new QueueDetailsResponse(
                      queueId, queue.getQueueName(), queue.getQueueCreationTimestamp());
              queue.getTokens().stream()
                  .filter(token -> token.getStatus() != TokenStatus.REMOVED)
                  .sorted(Comparator.comparing(Token::getTokenCreationTimestamp))
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
                    Long.valueOf(queue.getTokens().size()),
                    queue.getQueueCreationTimestamp()))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(
        queueRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .sorted(
                new Comparator<Queue>() {
                  public int compare(Queue a, Queue b) {
                    return a.getQueueCreationTimestamp().compareTo(b.getQueueCreationTimestamp());
                  }
                })
            .map(
                queue ->
                    new MyQueuesResponse.Queue(
                        queue.getQueueId(),
                        queue.getQueueName(),
                        queue.getQueueCreationTimestamp()))
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
                    Long.valueOf(queue.getTokens().size()),
                    queue.getQueueCreationTimestamp()))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }
}
