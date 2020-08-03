package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.QueueStatus;
import lombok.Data;

@Data
public class UpdateQueueStatusResponse {
    public final String queueId;
    public final String queueName;
    public final QueueStatus status;
}
