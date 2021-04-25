package me.simplq.utils.predicates;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import me.simplq.constants.QueueStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.dao.Queue;
import me.simplq.exceptions.SQAccessDeniedException;
import me.simplq.exceptions.SQInvalidRequestException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueThrowingPredicate {

  private static final String ACCESS_DENIED_ERROR_TEXT = "You do not have access to this queue";

  private final LoggedInUserInfo loggedInUserInfo;

  public Predicate<Queue> isNotPaused() {
    return queue -> {
      if (queue.getStatus() == QueueStatus.PAUSED) {
        throw SQInvalidRequestException.queuePausedException();
      }

      return true;
    };
  }

  public Predicate<Queue> isNotDeleted() {
    return queue -> {
      if (queue.getStatus() == QueueStatus.DELETED) {
        throw SQInvalidRequestException.queueDeletedException();
      }

      return true;
    };
  }

  public Predicate<Queue> isNotFull() {
    return queue -> {
      if (queue.isFull()) {
        throw SQInvalidRequestException.queueDeletedException();
      }

      return true;
    };
  }

  public Predicate<Queue> currentUserOwnsQueue() {
    return queue -> {
      if (queue.getOwner().getId().equals(loggedInUserInfo.getUserId())) {
        return true;
      }

      throw new SQAccessDeniedException(ACCESS_DENIED_ERROR_TEXT);
    };
  }
}
