package com.example.restservice.service.smsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(SmsManager.class);
  private final SmsService smsService;

  public SmsManager(SmsService smsService) {
    this.smsService = smsService;
  }

  /**
   * logic to implement invoke different SMS services as per need. currently fall back to Text Local
   * SMA service
   */
  public void notify(String contactNumber, String queueName) {
    smsService.sendSMS(contactNumber, queueName);
  }
}
