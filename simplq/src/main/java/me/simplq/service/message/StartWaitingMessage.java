package me.simplq.service.message;

class StartWaitingMessage implements Message {

  private static final String SUBJECT_FORMAT = "%s: You have been added to the queue.";
  private static final String BODY_FORMAT =
      "Hi %s,\n"
          + "You have been added to %s. Your token number is %d.\n"
          + "You can check your live status by visiting %s\n\n"
          + "Thanks for using simplq.me, a free and open source queue management software.";

  private final String name;
  private final String queueName;
  private final Integer tokenNumber;
  private final String tokenUrl;

  public StartWaitingMessage(String name, String queueName, Integer tokenNumber, String tokenUrl) {
    this.name = name;
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
    return String.format(BODY_FORMAT, name, queueName, tokenNumber, tokenUrl);
  }
}
