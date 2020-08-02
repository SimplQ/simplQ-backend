package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.QueueStatus;
import lombok.Data;

@Data
public class PauseQueueResponse {
    public final String queueId;
    public final String queueName;
    public final QueueStatus status;
}
