package me.simplq.service.smsService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.service.OwnerService;

@Slf4j
@Service
@Profile("prod")
public class CompanionAppSmsService implements SmsService {
  private static final String SMS_NUMBER_KEY = "SMS_NUMBER_KEY";
  private static final String SMS_PAYLOAD_KEY = "SMS_PAYLOAD";

  private final OwnerService ownerService;

  @Autowired
  public CompanionAppSmsService(OwnerService ownerService) {
    this.ownerService = ownerService;
    try {
      FirebaseApp.initializeApp(
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .build());
    } catch (IOException e) {
      // Env variable GOOGLE_APPLICATION_CREDENTIALS needs to be set. If you don't have the
      // credentials, disable this feature by setting sms.enabled=false in application.properties
      // If this exception occured while running a test, make sure that only 'test' spring profile
      // is active.
      //
      // https://firebase.google.com/docs/admin/setup?authuser=0#initialize-sdk
      throw new SQInternalServerException("FCM Credentials not set");
    }
  }

  @Override
  public void sendSMS(String contactNumber, String payload) {
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
                                .putData(SMS_NUMBER_KEY, contactNumber)
                                .putData(SMS_PAYLOAD_KEY, payload)
                                .setToken(deviceToken)
                                .build()));
              } catch (FirebaseMessagingException e) {
                throw new SQInternalServerException("Failed to send SMS", e);
              }
            });
  }
}
