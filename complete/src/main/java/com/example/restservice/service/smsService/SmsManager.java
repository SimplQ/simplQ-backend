package com.example.restservice.service.smsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsManager {
  private final SmsService smsService;

  @Value("${sms.enabled}")
  private boolean smsEnabled;

  /**
   * logic to implement invoke different SMS services as per need. currently fall back to Text Local
   * SMA service
   */
  public void notify(String contactNumber, String queueName) {
    if(!smsEnabled) {
      log.info("Sending sms has been disabled");
      return;
    }
    smsService.sendSMS(contactNumber, queueName);
  }
}
