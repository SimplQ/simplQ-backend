package me.simplq.service.smsService;

public interface SmsService {
  String sendSMS(String contactNumber, String queueName);
}
