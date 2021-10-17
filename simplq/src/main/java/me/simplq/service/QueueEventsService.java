package me.simplq.service;

import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueEventsResponse;
import me.simplq.controller.model.queue.QueueEventsResponse.Event;
import me.simplq.controller.model.queue.QueueEventsResponse.Event.EventType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class QueueEventsService {

  public QueueEventsResponse getQueueEvents(QueueDetailsResponse queueDetails) {
    return QueueEventsResponse.builder()
            .queueId(queueDetails.getQueueId())
            .queueName(queueDetails.getQueueName())
            .events(
                    getTokenEventsStream(queueDetails)
                            .sorted(Comparator.comparing(Event::getEventTimestamp).reversed())
                            .collect(Collectors.toList()))
            .build();
  }

  private Stream<Event> getTokenEventsStream(QueueDetailsResponse queueDetails) {
    // Make creation events for active tokens.
    var activeTokenEventStream =
            queueDetails.getTokens().stream()
                    .map(
                            token ->
                                    Event.builder()
                                            .eventType(EventType.TOKEN_ADDED)
                                            .tokenName(token.getName())
                                            .tokenNumber(token.getTokenNumber())
                                            .eventTimestamp(token.getTokenCreationTimestamp())
                                            .build());

    // Make creation and removal events for removed tokens.
    var removedTokenEventStream =
            queueDetails.getRemovedTokens().stream()
                    .map(
                            token ->
                                    List.of(
                                            Event.builder()
                                                    .eventType(EventType.TOKEN_ADDED)
                                                    .tokenName(token.getName())
                                                    .tokenNumber(token.getTokenNumber())
                                                    .eventTimestamp(token.getTokenCreationTimestamp())
                                                    .build(),
                                            Event.builder()
                                                    .eventType(EventType.TOKEN_REMOVED)
                                                    .tokenName(token.getName())
                                                    .tokenNumber(token.getTokenNumber())
                                                    .eventTimestamp(token.getTokenDeletionTimestamp())
                                                    .build()))
                    .flatMap(List::stream);

    return Stream.concat(activeTokenEventStream, removedTokenEventStream);
  }
}
