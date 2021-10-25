package me.simplq.service.message;

class EndWaitingMessage implements Message {

  private static final String SUBJECT_FORMAT = "%s: Hooray! your wait is finally over.";
  private static final String BODY_FORMAT =
      "Hi %s,\n\n"
          + "You have been notified by the queue admin. Your turn will be up soon.\n\n"
          + "Please proceed to the location now.\n\n"
          + FOOTER;

  private final String queueName;
  private final String tokenName;

  public EndWaitingMessage(String queueName, String tokenName) {
    this.queueName = queueName;
    this.tokenName = tokenName;
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
