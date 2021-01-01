package me.simplq.service.smsService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import me.simplq.exceptions.SQInternalServerException;

@Slf4j
public class CompanionAppSmsService implements SmsService {
  private static final String SMS_NUMBER_KEY = "SMS_NUMBER_KEY";
  private static final String SMS_PAYLOAD_KEY = "SMS_PAYLOAD";

  public CompanionAppSmsService() {
    try {
      FirebaseApp.initializeApp(
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .build());
    } catch (IOException e) {
      // Env variable GOOGLE_APPLICATION_CREDENTIALS needs to be set. If you don't have the
      // credentials, disable this feature by setting sms.enabled=false in application.properties
      // https://firebase.google.com/docs/admin/setup?authuser=0#initialize-sdk
      throw new SQInternalServerException("FCM Credentials not set");
    }
  }

  @Override
  public void sendSMS(String contactNumber, String payload) {
    // TODO Fetch from DB and send only if DB has a registered device
    String registrationToken =
        "dYAcu9akS0euPXAdeHSs3C:APA91bHVacmAlhAQzmJcGO7XjPlQhYlPHQj_xI-Lh9o8GKnPRMeBuMO-7OI1e6B8GG2IEqkTJqx11oI9ufalm6Sj-s9XDLCEDPaCgGCeskrVItc9pz78c4_FsBwlsxGp26kjtG3yv1ua";

    try {
      log.info(
          "Successfully sent message: {}",
          FirebaseMessaging.getInstance()
              .send(
                  Message.builder()
                      .putData(SMS_NUMBER_KEY, contactNumber)
                      .putData(SMS_PAYLOAD_KEY, payload)
                      .setToken(registrationToken)
                      .build()));
    } catch (FirebaseMessagingException e) {
      throw new SQInternalServerException("Failed to send SMS", e);
    }
  }
}
