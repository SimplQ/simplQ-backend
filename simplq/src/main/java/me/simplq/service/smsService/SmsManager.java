package me.simplq.service.smsService;

import java.util.ArrayList;
import java.util.List;
import me.simplq.service.OwnerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsManager {
  private final List<SmsService> smsServices;

  public SmsManager(@Value("${sms.enabled}") boolean smsEnabled, OwnerService ownerService) {
    smsServices = new ArrayList<>();
    smsServices.add(new MockSmsService());
    if (smsEnabled) {
      smsServices.add(new CompanionAppSmsService(ownerService));
    }
  }

  /**
   * logic to implement invoke different SMS services as per need. currently fall back to Text Local
   * SMA service
   */
  public void notify(String contactNumber, String payload) {
    smsServices.forEach(smsService -> smsService.sendSMS(contactNumber, payload));
  }
}
