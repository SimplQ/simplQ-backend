package com.example.restservice.service.smsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(SmsManager.class);

  /**
   * logic to implement invoke different SMS services as per need. currently fall back to Text Local
   * SMA service
   */
  public static void notify(String contactNumber, String queueName) {
    SmsService smsService = new TexLocalSmsService();
    smsService.sendSMS(contactNumber, queueName);
  }
}
