package com.example.restservice.service.smsService;

import com.example.restservice.config.PropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsManager {
  private final SmsService smsService;
  private final PropertiesConfig propertiesConfig;

  /**
   * logic to implement invoke different SMS services as per need. currently fall back to Text Local
   * SMA service
   */
  public void notify(String contactNumber, String queueName) {
    if(propertiesConfig.isSmsEnabled()) {
      smsService.sendSMS(contactNumber, queueName);
    }
  }
}
