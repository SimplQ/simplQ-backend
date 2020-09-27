package com.example.restservice.service;

import com.example.restservice.constants.TokenStatus;
import com.example.restservice.controller.advices.LoggedInUserInfo;
import com.example.restservice.controller.model.queue.CreateQueueRequest;
import com.example.restservice.controller.model.queue.CreateQueueResponse;
import com.example.restservice.controller.model.queue.MyQueuesResponse;
import com.example.restservice.controller.model.queue.QueueDetailsResponse;
import com.example.restservice.controller.model.queue.QueueStatusResponse;
import com.example.restservice.dao.CustomQuestions;
import com.example.restservice.dao.Queue;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.Token;
import com.example.restservice.exceptions.SQAccessDeniedException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

@Service
@RequiredArgsConstructor
public class QueueService {

  private final QueueRepository queueRepository;
  private final LoggedInUserInfo loggedInUserInfo;
  private final ObjectMapper mapper;

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
            {
                try {
                    CustomQuestions customQuestions = queue.getCustomQuestions() != null
                            ? mapper.readValue(queue.getCustomQuestions(), CustomQuestions.class)
                            : null;
                    return new QueueStatusResponse(
                        queueId,
                        queue.getQueueName(),
                        queue.getTokens().stream()
                            .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                            .count(),
                        Long.valueOf(queue.getTokens().size()),
                        queue.getQueueCreationTimestamp(),
                        customQuestions);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            })
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public MyQueuesResponse getMyQueues() {
    return new MyQueuesResponse(
        queueRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
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
            {
                try {
                    CustomQuestions customQuestions = queue.getCustomQuestions() != null
                            ? mapper.readValue(queue.getCustomQuestions(), CustomQuestions.class)
                            : null;
                    return new QueueStatusResponse(
                        queue.getQueueId(),
                        queue.getQueueName(),
                        queue.getTokens().stream()
                            .filter(user -> user.getStatus().equals(TokenStatus.WAITING))
                            .count(),
                        Long.valueOf(queue.getTokens().size()),
                        queue.getQueueCreationTimestamp(),
                        customQuestions);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            })
        .orElseThrow(SQInvalidRequestException::queueNotFoundException);
  }

  @Transactional
  public CustomQuestions createCustomQuestions(String queueId, String customQuestions) throws JsonProcessingException {
    CustomQuestions customQuestionsObject = mapper.readValue(customQuestions, CustomQuestions.class);
    var queue =  queueRepository
            .findById(queueId)
            .map(
                queue1 -> {
                  if (!queue1.getOwnerId().equals(loggedInUserInfo.getUserId())) {
                    throw new SQAccessDeniedException("You do not have access to this queue");
                  }
                    try {
                        queue1.setCustomQuestions(mapper.writeValueAsString(customQuestionsObject));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return queueRepository.save(queue1);
                })
            .orElseThrow(SQInvalidRequestException::queueNotFoundException);
    return customQuestionsObject;
  }
}
