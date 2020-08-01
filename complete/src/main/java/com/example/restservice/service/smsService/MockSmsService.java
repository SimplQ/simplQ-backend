package com.example.restservice.service.smsService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class MockSmsService implements SmsService {
    @Override
    public String sendSMS(String contactNumber, String queueName) {
        return "SUCCESS";
    }
}
