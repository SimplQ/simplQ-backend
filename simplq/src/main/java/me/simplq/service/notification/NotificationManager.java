package me.simplq.service.notification;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.simplq.dao.Token;
import me.simplq.service.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationManager {
  private final List<NotificationChannel> notificationChannels = new ArrayList<>();
  private final MockSmsChannel mockSmsChannel;
  private final AppNotificationChannel appNotificationChannel;

  @Value("${spring.profiles.active:Unknown}")
  private String activeProfile;

  @PostConstruct
  public void init() {
    notificationChannels.add(mockSmsChannel);
    notificationChannels.add(appNotificationChannel);

    if (!activeProfile.contains("local")) {
      // Disable SES Email channel as it requires AWS, which is not available on local.
      notificationChannels.add(new SesEmailNotificationChannel());
    }
  }

  /** Pass notification to all enabled channels */
  public void notify(Token token, Message message) {
    notificationChannels.forEach(notificationChannel -> notificationChannel.notify(token, message));
  }
}
