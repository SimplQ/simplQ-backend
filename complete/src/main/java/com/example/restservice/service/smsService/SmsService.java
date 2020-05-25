package com.example.restservice.service.smsService;

public interface SmsService {
    String sendSMS(String contactNumber, String queueName);
}
