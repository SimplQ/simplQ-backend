package me.simplq.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.simplq.constants.QueueStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.controller.model.queue.PatchQueueRequest;
import me.simplq.dao.Owner;
import me.simplq.dao.Queue;
import me.simplq.dao.QueueRepository;
import me.simplq.exceptions.SQInvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {

  @Mock QueueRepository repository;

  @Mock OwnerService ownerService;

  @Mock LoggedInUserInfo loggedInUserInfo;

  QueueService queueService;

  @BeforeEach
  public void setUp() {
    queueService = new QueueService(repository, ownerService, loggedInUserInfo);
  }

  @DisplayName("Throw queue not found exception if queue does not exists")
  @Test
  void throwExceptionIfQueueDoesNotExists() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());

    var patchRequest = new PatchQueueRequest();

    Executable execute = () -> queueService.updateMaxQueueCapacity("queueId", patchRequest);

    SQInvalidRequestException assertThrows = assertThrows(SQInvalidRequestException.class, execute);

    assertThat(assertThrows.getMessage()).isEqualTo("The queue does not exist");
  }

  @DisplayName("Allow user to change queue max capacity when queue exists")
  @Test
  void addQueueMaxCapacity() {
    var queue = new Queue("queue", new Owner(), QueueStatus.ACTIVE);

    when(repository.findById(anyString())).thenReturn(Optional.of(queue));
    when(repository.save(any(Queue.class))).thenReturn(queue);

    var patchRequest = new PatchQueueRequest();
    patchRequest.setMaxQueueCapacity(10);

    queueService.updateMaxQueueCapacity("queueId", patchRequest);

    verify(repository).save(eq(queue));
    assertThat(queue.getMaxQueueCapacity()).isEqualTo(10);
  }
}
