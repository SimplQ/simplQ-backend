package com.example.restservice.service;


import com.example.restservice.dao.QueueDAO;
import com.example.restservice.model.Queue;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    @Autowired
    private QueueDAO queueDAO;

    public Queue generateQueueId(String queueName){
        Queue queue = new Queue();
        String id = generateRandomAlphaNumericString();
        queue.setQueueName(queueName);
        queue.setQueueId(id);
        //queueDAO.addQueueData(queue);
        return queue;
    }

    private String generateRandomAlphaNumericString() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        return  generatedString;
    }
}
