package com.example.restservice.exceptions;

public class SQInternalServerException extends SQException {

  public SQInternalServerException() {}

  public SQInternalServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQInternalServerException(String message) {
    super(message);
  }
}
