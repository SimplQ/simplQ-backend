package me.simplq.service.message;

class StartWaitingMessage implements Message {

  private static final String FORMAT =
      "Hi %s,\n"
          + "You have been added to %s. Your token number is %d. You can know your status visiting"
          + " %s\n"
          + "Thanks for using simplq.me";

  private final String name;
  private final String queue;
  private final Integer tokenNumber;
  private final String tokenUrl;

  public StartWaitingMessage(String name, String queue, Integer tokenNumber, String tokenUrl) {
    this.name = name;
    this.queue = queue;
    this.tokenNumber = tokenNumber;
    this.tokenUrl = tokenUrl;
  }

  @Override
  public String text() {
    return String.format(FORMAT, name, queue, tokenNumber, tokenUrl);
  }
}
