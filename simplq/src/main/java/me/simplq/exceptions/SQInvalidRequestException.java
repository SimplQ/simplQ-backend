package me.simplq.exceptions;

import java.util.HashMap;
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
    QUEUE_IS_FULL,
    ONLY_OWNER_CAN_CREATE_TOKEN,
    TOKEN_ALREADY_WAITING;
  }

  // TODO Move to SQException and include internal server ones too.
  private static final Map<ReasonCode, String> message = new HashMap<>();

  static {
    message.put(ReasonCode.QUEUE_NOT_FOUND, "The queue does not exist");
    message.put(ReasonCode.TOKEN_NOT_FOUND, "The token does not exist");
    message.put(ReasonCode.QUEUE_NAME_ALREADY_EXISTS, "The queue name already exists");
    message.put(ReasonCode.TOKEN_NOT_NOTIFIABLE, "Only tokens with WAITING status can be notified");
    message.put(ReasonCode.TOKEN_DELETED, "The token has been deleted from the queue");
    message.put(ReasonCode.QUEUE_PAUSED, "The queue has been paused");
    message.put(ReasonCode.QUEUE_DELETED, "The queue has been deleted");
    message.put(ReasonCode.QUEUE_DELETE_NOT_ALLOWED, "Delete not allowed in pause request");
    message.put(ReasonCode.QUEUE_IS_FULL, "The queue is full, please try again after sometime");
    message.put(ReasonCode.ONLY_OWNER_CAN_CREATE_TOKEN,
        "Only queue owner can create tokens for this queue");
    message.put(ReasonCode.TOKEN_ALREADY_WAITING, "Sorry, you are already present in the queue.");
  }


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

  public static SQInvalidRequestException onlyOwnerCanCreateTokens() {
    return new SQInvalidRequestException(ReasonCode.ONLY_OWNER_CAN_CREATE_TOKEN);
  }
  
  public static SQInvalidRequestException tokenAlreadyWaiting() {
    return new SQInvalidRequestException(ReasonCode.TOKEN_ALREADY_WAITING);
  }

  public ReasonCode getReasonCode() {
    return reasonCode;
  }

  public String getMessage() {
    return message.get(reasonCode);
  }
}
