package com.example.restservice.exceptions;

import com.google.common.collect.ImmutableMap;

public class SQInvalidRequestException extends SQException {
  private enum ReasonCode {
    QUEUE_NOT_FOUND,
    USER_NOT_FOUND,
    QUEUE_NAME_ALREADY_EXISTS
  }

  private final ImmutableMap<ReasonCode, String> message =
      ImmutableMap.of(
          ReasonCode.QUEUE_NOT_FOUND, "The queue does not exist",
          ReasonCode.USER_NOT_FOUND, "The user does not exist",
          ReasonCode.QUEUE_NAME_ALREADY_EXISTS, "The queue name already exists"
      );

  private final ReasonCode reasonCode;

  private SQInvalidRequestException(ReasonCode reasonCode) {
    this.reasonCode = reasonCode;
  }

  public static SQInvalidRequestException queueNotFoundException() {
    return new SQInvalidRequestException(ReasonCode.QUEUE_NOT_FOUND);
  }

  public static SQInvalidRequestException userNotFoundException() {
    return new SQInvalidRequestException(ReasonCode.USER_NOT_FOUND);
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
