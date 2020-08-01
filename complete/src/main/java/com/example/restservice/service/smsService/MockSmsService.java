package com.example.restservice.service.smsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class MockSmsService implements SmsService {
    private final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

    @Override
    public String sendSMS(String contactNumber, String queueName) {
        LOGGER.warn("Mock SMS Sent");
        return "SUCCESS";
    }
}
