package com.example.restservice.exceptions;

import com.google.common.collect.ImmutableMap;

public class SQInvalidRequestException extends SQException {

  private enum ReasonCode {
    QUEUE_NOT_FOUND,
    TOKEN_NOT_FOUND,
    QUEUE_NAME_ALREADY_EXISTS,
    TOKEN_NOT_NOTIFIABLE;
  }

  private final ImmutableMap<ReasonCode, String> message =
      ImmutableMap.of(
          ReasonCode.QUEUE_NOT_FOUND, "The queue does not exist",
          ReasonCode.TOKEN_NOT_FOUND, "The token does not exist",
          ReasonCode.QUEUE_NAME_ALREADY_EXISTS, "The queue name already exists",
          ReasonCode.TOKEN_NOT_NOTIFIABLE, "Only tokens with WAITING status can be notified");

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

  public ReasonCode getReasonCode() {
    return reasonCode;
  }

  public String getMessage() {
    return message.get(reasonCode);
  }
}
