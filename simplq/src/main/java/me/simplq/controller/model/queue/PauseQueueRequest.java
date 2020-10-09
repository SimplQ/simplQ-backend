package com.example.restservice.controller.model.queue;

import com.example.restservice.constants.QueueStatus;
import lombok.Data;

@Data
public class PauseQueueRequest {
    QueueStatus status;
}
