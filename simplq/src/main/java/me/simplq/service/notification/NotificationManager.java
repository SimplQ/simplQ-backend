package me.simplq.service.notification;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.simplq.dao.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationManager {
  private final List<NotificationChannel> notificationChannels = new ArrayList<>();
  private final MockSmsChannel mockSmsChannel;

  @Value("${spring.profiles.active:Unknown}")
  private String activeProfile;

  @PostConstruct
  public void init() {
    notificationChannels.add(mockSmsChannel);
    if (!activeProfile.contains("local")) {
      // Disable SES Email channel as it requires AWS, which is not available on local.
      notificationChannels.add(new SesEmailNotificationChannel());
    }
  }

  /** Pass notification to all enabled channels */
  public void notify(Token token, String payload) {
    notificationChannels.forEach(notificationChannel -> notificationChannel.notify(token, payload));
  }
}
