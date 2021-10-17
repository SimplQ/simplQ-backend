package me.simplq.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.simplq.constants.QueueStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.controller.model.queue.CreateQueueRequest;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.PatchQueueRequest;
import me.simplq.controller.model.queue.PatchQueueResponse;
import me.simplq.controller.model.queue.PauseQueueRequest;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueEventsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
import me.simplq.controller.model.queue.UpdateQueueStatusResponse;
import me.simplq.dao.Queue;
import me.simplq.dao.QueueRepository;
import me.simplq.exceptions.SQInvalidRequestException;
import me.simplq.utils.predicates.QueueThrowingPredicate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

  private final QueueRepository queueRepository;
  private final OwnerService ownerService;
  private final LoggedInUserInfo loggedInUserInfo;
  private final QueueThrowingPredicate queueThrowingPredicate;
  private final QueueEventsService queueEventsService;

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
    return queueRepository
        .findById(queueId)
        .map(
            queue1 -> {
              queueThrowingPredicate
                  .isNotDeleted()
                  .and(queueThrowingPredicate.currentUserOwnsQueue())
                  .test(queue1);
              if (pauseQueueRequest.getStatus().equals(QueueStatus.DELETED)) {
                throw SQInvalidRequestException.queueDeletedNotAllowedException();
              }
              queue1.setStatus(pauseQueueRequest.getStatus());
              return queueRepository.save(queue1);
            })
        .map(UpdateQueueStatusResponse::fromEntity)
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public UpdateQueueStatusResponse deleteQueue(String queueId) {
    return queueRepository
        .findById(queueId)
        .filter(
            queueThrowingPredicate
                .isNotDeleted()
                .and(queueThrowingPredicate.currentUserOwnsQueue()))
        .map(
            queue1 -> {
              queue1.setStatus(QueueStatus.DELETED);
              return UpdateQueueStatusResponse.fromEntity(queueRepository.save(queue1));
            })
        .orElseThrow(SQInvalidRequestException::tokenNotFoundException);
  }

  @Transactional
  public QueueDetailsResponse getQueueDetails(String queueId) {
    return getQueueDetailsResponseInternal(queueId);
  }

  @Transactional
  public QueueStatusResponse getQueueStatus(String queueId) {
    return queueRepository
        .findById(queueId)
        .map(QueueStatusResponse::fromEntity)
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(
        queueRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .filter(queue -> !QueueStatus.DELETED.equals(queue.getStatus()))
            .sorted(Comparator.comparing(Queue::getQueueCreationTimestamp))
            .map(MyQueuesResponse.Queue::fromEntity)
            .collect(Collectors.toList()));
  }

  @Transactional
  public QueueStatusResponse getQueueStatusByName(String queueName) {
    return queueRepository
        .findByQueueName(queueName)
        .map(QueueStatusResponse::fromEntity)
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public PatchQueueResponse patchQueue(String queueId, PatchQueueRequest patchRequest) {
    return queueRepository
        .findById(queueId)
        .map(patchQueue(patchRequest))
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  private Function<Queue, PatchQueueResponse> patchQueue(PatchQueueRequest patchQueueRequest) {
    return queue -> {
      var response = PatchQueueResponse.builder();
      Optional.ofNullable(patchQueueRequest.getMaxQueueCapacity())
          .ifPresent(
              maxQueueCapacity -> {
                queue.setMaxQueueCapacity(maxQueueCapacity);
                response.maxQueueCapacity(maxQueueCapacity.longValue());
              });

      Optional.ofNullable(patchQueueRequest.getIsSelfJoinAllowed())
          .ifPresent(
              isSelfJoinAllowed -> {
                queue.setSelfJoinAllowed(isSelfJoinAllowed);
                response.isSelfJoinAllowed(isSelfJoinAllowed);
              });

      Optional.ofNullable(patchQueueRequest.getNotifyByEmail())
          .ifPresent(
              notifyByEmail -> {
                queue.setNotifyByEmail(notifyByEmail);
                response.notifyByEmail(notifyByEmail);
              });

      var updatedQueue = queueRepository.save(queue);

      return response
          .queueName(updatedQueue.getQueueName())
          .queueId(updatedQueue.getQueueId())
          .build();
    };
  }

  @Transactional
  public QueueEventsResponse getQueueEvents(String queueId) {
    return queueEventsService.getQueueEvents(this.getQueueDetailsResponseInternal(queueId));
  }

  private QueueDetailsResponse getQueueDetailsResponseInternal(String queueId) {
    return queueRepository
        .findById(queueId)
        .filter(queueThrowingPredicate.currentUserOwnsQueue())
        .map(QueueDetailsResponse::fromEntity)
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }
}
