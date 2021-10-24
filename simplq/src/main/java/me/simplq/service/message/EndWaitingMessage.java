package me.simplq.service.message;

class EndWaitingMessage implements Message {

  private static final String SUBJECT_FORMAT = "%s: Hooray! your wait is finally over.";
  private static final String BODY_FORMAT = "You have been notified by the queue manager.";

  private final String queueName;

  public EndWaitingMessage(String queueName) {
    this.queueName = queueName;
  }

  @Override
  public String subject() {
    return String.format(SUBJECT_FORMAT, queueName);
  }

  @Override
  public String body() {
    return String.format(BODY_FORMAT, queueName);
  }
}
