package me.simplq.service.message;

import static me.simplq.service.message.StripHtml.stripHtml;

class EndWaitingMessage implements Message {

  private static final String SUBJECT_FORMAT = "%s: Hooray! your wait is finally over.";
  private static final String MAIN_MESSAGE = "Please proceed to the location now.";
  private static final String BODY_FORMAT =
      "<p>Hi %s,</p>"
          + "<p>You have been notified by the queue admin. Your turn will be up soon.</p>"
          + "<p><b>"
          + MAIN_MESSAGE
          + "</b></p>"
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
    return stripHtml(bodyHtml());
  }

  @Override
  public String bodyHtml() {
    return String.format(BODY_FORMAT, tokenName);
  }

  @Override
  public String shortBody() {
    return MAIN_MESSAGE;
  }

  @Override
  public Boolean isPriority() {
    return Boolean.TRUE;
  }
}
