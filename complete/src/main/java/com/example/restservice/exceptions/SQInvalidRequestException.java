package com.example.restservice.exceptions;

public class SQInvalidRequestException extends SQException {
  private enum ReasonCode {
    QUEUE_NOT_FOUND,
    USER_NOT_FOUND
  }

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

  public ReasonCode getReasonCode() {
    return reasonCode;
  }
}
