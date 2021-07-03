package me.simplq.service.smsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"local", "dev"})
public class MockSmsService implements SmsService {
  @Override
  public void sendSMS(String contactNumber, String payload) {
    log.info("Mock SMS Sent: {}", payload);
  }
}
