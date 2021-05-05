package me.simplq.service.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class MessagesManager {

  private final String tokenUrlPrefix;

  public MessagesManager(@Value("${token.url}") String tokenUrlPrefix) {
    this.tokenUrlPrefix = tokenUrlPrefix;
  }

  public Message endWaiting(String queue) {
    return new EndWaitingMessage(queue);
  }

  public Message startWaiting(String name, String queueName, Integer tokenNumber, String tokenId) {
    return new StartWaitingMessage(name, queueName, tokenNumber, tokenUrl(tokenId));
  }

  private String tokenUrl(String tokenId) {
    return URI.create(tokenUrlPrefix).resolve(tokenId).toString();
  }
}
