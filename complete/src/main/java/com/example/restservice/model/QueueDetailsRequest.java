package com.example.restservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueDetailsRequest {
    String queueId;

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }
}
