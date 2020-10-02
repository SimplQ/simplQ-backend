package me.simplq.exceptions;

/** Base exception class that all our custom exceptions will extend */
public abstract class SQException extends RuntimeException {

  public SQException() {
    super();
  }

  public SQException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQException(String message) {
    super(message);
  }
}
