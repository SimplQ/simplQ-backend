package me.simplq.service.smsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!prod")
public class MockSmsService implements SmsService {
  @Override
  public String sendSMS(String contactNumber, String queueName) {
    log.warn("Mock SMS Sent");
    return "SUCCESS";
  }
}
