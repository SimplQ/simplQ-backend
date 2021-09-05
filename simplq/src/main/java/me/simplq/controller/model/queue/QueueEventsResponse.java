package me.simplq.controller.model.queue;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

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

  List<Event> events;
}
