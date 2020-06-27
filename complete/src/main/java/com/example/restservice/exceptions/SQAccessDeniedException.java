package com.example.restservice.exceptions;

public class SQAccessDeniedException extends SQException {

  public SQAccessDeniedException(String message) {
    super(message);
  }
}
