package me.simplq.service.message;

class EndWaitingMessage implements Message {

  private static final String FORMAT = "Hi, your wait for %s is over! You can proceed";

  private final String queue;

  public EndWaitingMessage(String queue) {
    this.queue = queue;
  }

  @Override
  public String text() {
    return String.format(FORMAT, queue);
  }
}
