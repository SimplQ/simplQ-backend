package me.simplq.service.smsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockSmsService implements SmsService {
  @Override
  public void sendSMS(String contactNumber, String payload) {
    log.info("Mock SMS Sent: {}", payload);
  }
}
