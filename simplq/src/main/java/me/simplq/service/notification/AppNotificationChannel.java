package me.simplq.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Device;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.service.OwnerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppNotificationChannel implements NotificationChannel {
  private static final String TITLE_KEY = "TITLE";
  private static final String BODY_KEY = "BODY";

  private final OwnerService ownerService;

  @Value("${app.notifications.enabled}")
  private Boolean isAppNotificationsEnabled;

  @PostConstruct
  public void init() {
    if (!isAppNotificationsEnabled) {
      return;
    }

    try {
      FirebaseApp.initializeApp(
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .build());
    } catch (IOException e) {
      // Env variable GOOGLE_APPLICATION_CREDENTIALS needs to be set.
      // If this exception occured while running a test, make sure that only 'test' spring profile
      // is active.
      //
      // If this exception happened locally and you don't have the credentials, make sure
      // app.notifications.enabled is set to false.
      //
      // If this exception happened on prod or you have the credentials, read below page on how to
      // set it
      // to environment variable:
      // https://firebase.google.com/docs/admin/setup?authuser=0#initialize-sdk
      throw new SQInternalServerException("FCM Credentials not set", e);
    }
  }

  @Override
  public void notify(Token token, me.simplq.service.message.Message message) {
    if (!isAppNotificationsEnabled) {
      return;
    }

    // Notify users only of important messages
    if (!message.isPriority()) {
      return;
    }

    ownerService
        .getDevices(token.getOwnerId())
        .map(Device::getId)
        .forEach(
            deviceToken -> {
              try {
                log.info(
                    "Successfully sent message: {}",
                    FirebaseMessaging.getInstance()
                        .send(
                            Message.builder()
                                .putData(TITLE_KEY, message.subject())
                                .putData(BODY_KEY, message.shortBody())
                                .setToken(deviceToken)
                                .build()));
              } catch (FirebaseMessagingException e) {
                throw new SQInternalServerException("Failed to send app notification", e);
              }
            });
  }
}
