package me.simplq.service.smsService;

public interface SmsService {
  void sendSMS(String contactNumber, String payload);
}
