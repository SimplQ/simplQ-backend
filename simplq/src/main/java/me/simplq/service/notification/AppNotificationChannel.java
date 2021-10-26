package me.simplq.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Device;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.service.OwnerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppNotificationChannel implements NotificationChannel {
  private static final String TITLE_KEY = "SMS_NUMBER_KEY";
  private static final String BODY_KEY = "SMS_PAYLOAD";

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
      // https://firebase.google.com/docs/admin/setup?authuser=0#initialize-sdk
      throw new SQInternalServerException("FCM Credentials not set", e);
    }
  }

  @Override
  public void notify(Token token, me.simplq.service.message.Message message) {
    if (!isAppNotificationsEnabled) {
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
                                                        .putData(TITLE_KEY, token.getContactNumber())
                                                        .putData(BODY_KEY, message.body())
                                                        .setToken(deviceToken)
                                                        .build()));
                      } catch (FirebaseMessagingException e) {
                        throw new SQInternalServerException("Failed to send SMS", e);
                      }
                    });
  }
}
