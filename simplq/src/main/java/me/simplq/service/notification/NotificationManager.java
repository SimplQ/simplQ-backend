package me.simplq.service.notification;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.simplq.dao.Token;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationManager {
  private final List<NotificationChannel> notificationChannels = new ArrayList<>();

  @PostConstruct
  public void init() {
    notificationChannels.add(new MockSmsService());
  }

  /** Pass notification to all enabled channels */
  public void notify(Token token, String payload) {
    notificationChannels.forEach(notificationChannel -> notificationChannel.notify(token, payload));
  }
}
