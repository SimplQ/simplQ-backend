package me.simplq.service.smsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockSmsService implements SmsService {
  @Override
  public void sendSMS(String contactNumber, String queueName) {
    log.info("Mock SMS Sent");
  }
}
