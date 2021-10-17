package me.simplq.service.notification;

import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Token;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"local", "dev"})
public class MockSmsService implements NotificationChannel {
  @Override
  public void notify(Token token, String payload) {
    log.info("Mock SMS Sent: {}", payload);
  }
}
