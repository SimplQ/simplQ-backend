package me.simplq.exceptions;

import java.util.Map;

public class SQInvalidRequestException extends SQException {

  private enum ReasonCode {
    QUEUE_NOT_FOUND,
    TOKEN_NOT_FOUND,
    QUEUE_NAME_ALREADY_EXISTS,
    TOKEN_NOT_NOTIFIABLE,
    TOKEN_DELETED,
    QUEUE_PAUSED,
    QUEUE_DELETED,
    QUEUE_DELETE_NOT_ALLOWED,
    QUEUE_IS_FULL;
  }

  // TODO Move to SQException and include internal server ones too.
  private final Map<ReasonCode, String> message =
      Map.of(
          ReasonCode.QUEUE_NOT_FOUND, "The queue does not exist",
          ReasonCode.TOKEN_NOT_FOUND, "The token does not exist",
          ReasonCode.QUEUE_NAME_ALREADY_EXISTS, "The queue name already exists",
          ReasonCode.TOKEN_NOT_NOTIFIABLE, "Only tokens with WAITING status can be notified",
          ReasonCode.TOKEN_DELETED, "The token has been deleted from the queue",
          ReasonCode.QUEUE_PAUSED, "The queue has been paused",
          ReasonCode.QUEUE_DELETED, "The queue has been deleted",
          ReasonCode.QUEUE_DELETE_NOT_ALLOWED, "Delete not allowed in pause request",
          ReasonCode.QUEUE_IS_FULL, "The queue is full, please try again after sometime");

  private final ReasonCode reasonCode;

  private SQInvalidRequestException(ReasonCode reasonCode) {
    this.reasonCode = reasonCode;
  }

  public static SQInvalidRequestException queueNotFoundException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_NOT_FOUND);
  }

  public static SQInvalidRequestException tokenNotFoundException() {
    return new SQInvalidRequestException(ReasonCode.TOKEN_NOT_FOUND);
  }

  public static SQInvalidRequestException tokenNotNotifiableException() {
    return new SQInvalidRequestException(ReasonCode.TOKEN_NOT_NOTIFIABLE);
  }

  public static SQInvalidRequestException queueNameNotUniqueException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_NAME_ALREADY_EXISTS);
  }

  public static SQInvalidRequestException tokenDeletedException() {
    return new SQInvalidRequestException(ReasonCode.TOKEN_DELETED);
  }

  public static SQInvalidRequestException queuePausedException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_PAUSED);
  }

  public static SQInvalidRequestException queueDeletedException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_DELETED);
  }

  public static SQInvalidRequestException queueDeletedNotAllowedException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_DELETE_NOT_ALLOWED);
  }

  public static SQInvalidRequestException queueIsFullException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_IS_FULL);
  }

  public ReasonCode getReasonCode() {
    return reasonCode;
  }

  public String getMessage() {
    return message.get(reasonCode);
  }
}
