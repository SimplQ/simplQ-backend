package me.simplq.controller.model.queue;

import me.simplq.constants.QueueStatus;
import lombok.Data;

@Data
public class UpdateQueueStatusResponse {
    public final String queueId;
    public final String queueName;
    public final QueueStatus status;
}
