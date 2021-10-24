package me.simplq.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("companion-app")
public class CompanionAppNotificationChannel implements NotificationChannel {
  private static final String SMS_NUMBER_KEY = "SMS_NUMBER_KEY";
  private static final String SMS_PAYLOAD_KEY = "SMS_PAYLOAD";

  private final OwnerService ownerService;

  @Autowired
  public CompanionAppNotificationChannel(OwnerService ownerService) {
    this.ownerService = ownerService;
    try {
      FirebaseApp.initializeApp(
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .build());
    } catch (IOException e) {
      // Env variable GOOGLE_APPLICATION_CREDENTIALS needs to be set. If you don't have the
      // credentials, disable this feature by setting sms.enabled=false in application.properties
      // If this exception occured while running a test, make sure that only 'test' spring profile
      // is active.
      //
      // https://firebase.google.com/docs/admin/setup?authuser=0#initialize-sdk
      throw new SQInternalServerException("FCM Credentials not set", e);
    }
  }

  @Override
  public void notify(Token token, me.simplq.service.message.Message message) {
    ownerService
        .getDeviceToken()
        .ifPresent(
            deviceToken -> {
              try {
                log.info(
                    "Successfully sent message: {}",
                    FirebaseMessaging.getInstance()
                        .send(
                            Message.builder()
                                .putData(SMS_NUMBER_KEY, token.getContactNumber())
                                .putData(SMS_PAYLOAD_KEY, message.body())
                                .setToken(deviceToken)
                                .build()));
              } catch (FirebaseMessagingException e) {
                throw new SQInternalServerException("Failed to send SMS", e);
              }
            });
  }
}
