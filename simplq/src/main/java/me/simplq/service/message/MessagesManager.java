package me.simplq.service.message;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagesManager {

  private final String tokenUrlPrefix;

  public MessagesManager(@Value("${token.url}") String tokenUrlPrefix) {
    this.tokenUrlPrefix = tokenUrlPrefix;
  }

  public Message endWaiting(String queueName, String tokenName) {
    return new EndWaitingMessage(queueName, tokenName);
  }

  public Message startWaiting(
      String tokenName, String queueName, Integer tokenNumber, String tokenId) {
    return new StartWaitingMessage(tokenName, queueName, tokenNumber, tokenUrl(tokenId));
  }

  private String tokenUrl(String tokenId) {
    return URI.create(tokenUrlPrefix).resolve(tokenId).toString();
  }
}
