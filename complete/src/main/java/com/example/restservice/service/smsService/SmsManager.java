package com.example.restservice.service.smsService;

public class SmsManager {

    /**
     * logic to implement invoke different SMS services as per need.
     * currently fall back to Text Local SMA service
     */
    public static void notify(String contactNumber, String queueName) {
        SmsService smsService = new TexLocalSmsService();
        smsService.sendSMS(contactNumber, queueName);
    }
}
