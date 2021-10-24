package me.simplq.service.notification;

import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Token;
import me.simplq.service.message.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockSmsChannel implements NotificationChannel {
  @Override
  public void notify(Token token, Message message) {
    log.info("Mock SMS Sent: subject:{}, body:{}", message.subject(), message.body());
  }
}
