package me.simplq.service;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.controller.model.queue.CreateQueueRequest;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.PatchQueueRequest;
import me.simplq.controller.model.queue.PatchQueueResponse;
import me.simplq.controller.model.queue.PauseQueueRequest;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
import me.simplq.controller.model.queue.UpdateQueueStatusResponse;
import me.simplq.dao.Queue;
import me.simplq.dao.QueueRepository;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQAccessDeniedException;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.exceptions.SQInvalidRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

  private static final String ACCESS_DENIED_ERROR_TEXT = "You do not have access to this queue";
  private final QueueRepository queueRepository;
  private final OwnerService ownerService;
  private final LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public CreateQueueResponse createQueue(CreateQueueRequest createQueueRequest) {
    var owner = ownerService.getOwnerOrElseCreate();
    try {
      var queue =
          queueRepository.saveAndFlush(
              new Queue(createQueueRequest.getQueueName(), owner, QueueStatus.ACTIVE));
      return new CreateQueueResponse(queue.getQueueName(), queue.getQueueId());
    } catch (DataIntegrityViolationException de) {
      throw SQInvalidRequestException.queueNameNotUniqueException();
    }
  }

  @Transactional
  public UpdateQueueStatusResponse pauseQueue(PauseQueueRequest pauseQueueRequest, String queueId) {
    try {
      var queue =
          queueRepository
              .findById(queueId)
              .map(
                  queue1 -> {
                    if (!queue1.getOwner().getId().equals(loggedInUserInfo.getUserId())) {
                      throw new SQAccessDeniedException(ACCESS_DENIED_ERROR_TEXT);
                    }
                    if (queue1.getStatus().equals(QueueStatus.DELETED)) {
                      throw SQInvalidRequestException.queueDeletedException();
                    }
                    if (pauseQueueRequest.getStatus().equals(QueueStatus.DELETED)) {
                      throw SQInvalidRequestException.queueDeletedNotAllowedException();
                    }
                    queue1.setStatus(pauseQueueRequest.getStatus());
                    return queueRepository.save(queue1);
                  })
              .orElseThrow();
      return new UpdateQueueStatusResponse(
          queue.getQueueId(), queue.getQueueName(), queue.getStatus());
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to update queue: ", e);
    }
  }

  @Transactional
  public UpdateQueueStatusResponse deleteQueue(String queueId) {
    try {
      var queue =
          queueRepository
              .findById(queueId)
              .map(
                  queue1 -> {
                    if (!queue1.getOwner().getId().equals(loggedInUserInfo.getUserId())) {
                      throw new SQAccessDeniedException(ACCESS_DENIED_ERROR_TEXT);
                    }
                    if (queue1.getStatus().equals(QueueStatus.DELETED)) {
                      throw SQInvalidRequestException.queueDeletedException();
                    }
                    queue1.setStatus(QueueStatus.DELETED);
                    return queueRepository.save(queue1);
                  })
              .orElseThrow();
      return new UpdateQueueStatusResponse(
          queue.getQueueId(), queue.getQueueName(), queue.getStatus());
    } catch (Exception e) {
      throw new SQInternalServerException("Unable to delete queue: ", e);
    }
  }

  @Transactional
  public QueueDetailsResponse getQueueDetails(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(
            queue -> {
              if (!queue.getOwner().getId().equals(loggedInUserInfo.getUserId())) {
                throw new SQAccessDeniedException(ACCESS_DENIED_ERROR_TEXT);
              }
              var resp =
                  new QueueDetailsResponse(
                      queueId,
                      queue.getQueueName(),
                      queue.getQueueCreationTimestamp(),
                      queue.getStatus(),
                      queue.getMaxQueueCapacity(),
                      queue.getSlotsLeft());
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
                    queue.getStatus(),
                    queue.getMaxQueueCapacity(),
                    queue.getTokens().stream()
                        .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                        .count(),
                    Long.valueOf(queue.getTokens().size()),
                    queue.getSlotsLeft(),
                    queue.getQueueCreationTimestamp()))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(
        queueRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .filter(queue -> queue.getStatus() != QueueStatus.DELETED)
            .sorted(Comparator.comparing(Queue::getQueueCreationTimestamp))
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
                    queue.getStatus(),
                    queue.getMaxQueueCapacity(),
                    queue.getTokens().stream()
                        .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                        .count(),
                    Long.valueOf(queue.getTokens().size()),
                    queue.getSlotsLeft(),
                    queue.getQueueCreationTimestamp()))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public PatchQueueResponse updateMaxQueueCapacity(String queueId, PatchQueueRequest patchRequest) {
    return queueRepository
        .findById(queueId)
        .map(updateMaxQueueCapacity(patchRequest))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  private Function<Queue, PatchQueueResponse> updateMaxQueueCapacity(
      PatchQueueRequest patchQueueRequest) {

    return queue -> {
      queue.setMaxQueueCapacity(patchQueueRequest.getMaxQueueCapacity());
      var updatedQueue = queueRepository.save(queue);

      return PatchQueueResponse.builder()
          .queueName(updatedQueue.getQueueName())
          .queueId(updatedQueue.getQueueId())
          .maxQueueCapacity(updatedQueue.getMaxQueueCapacity())
          .build();
    };
  }
}
