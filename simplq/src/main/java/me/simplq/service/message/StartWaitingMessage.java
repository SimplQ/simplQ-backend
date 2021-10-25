package me.simplq.service.message;

class StartWaitingMessage implements Message {

  private static final String SUBJECT_FORMAT = "%s: You have been added to the queue.";
  private static final String BODY_FORMAT =
      "Hi %s,\n\n"
          + "You have been added to queue %s. Your token number is %s.\n\n"
          + "You can check your live status by visiting %s\n\n"
          + "<b>Please wait to be notified before you visit the location. Stay away from crowds and"
          + " have a delightful experience.</b>\n\n"
          + FOOTER;

  private final String tokenName;
  private final String queueName;
  private final Integer tokenNumber;
  private final String tokenUrl;

  public StartWaitingMessage(
      String tokenName, String queueName, Integer tokenNumber, String tokenUrl) {
    this.tokenName = tokenName;
    this.queueName = queueName;
    this.tokenNumber = tokenNumber;
    this.tokenUrl = tokenUrl;
  }

  @Override
  public String subject() {
    return String.format(SUBJECT_FORMAT, queueName);
  }

  @Override
  public String body() {
    return String.format(BODY_FORMAT, tokenName, queueName, tokenNumber, tokenUrl);
  }
}
