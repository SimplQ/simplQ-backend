package me.simplq.controller.model.queue;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class QueueEventsResponse {
    @Data
    @Builder
    public static class Event {
        public enum EventType {
            TOKEN_ADDED,
            TOKEN_REMOVED
        }

        EventType eventType;
        String tokenName;
        Integer tokenNumber;
        Date eventTimestamp;
    }

    String queueId;
    String queueName;
    List<Event> events;
}
