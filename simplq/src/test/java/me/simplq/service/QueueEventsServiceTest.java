package me.simplq.service;

import java.text.SimpleDateFormat;
import java.util.List;
import lombok.SneakyThrows;
import me.simplq.constants.TokenStatus;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueEventsResponse.Event.EventType;
import me.simplq.dao.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class QueueEventsServiceTest {
  private final QueueEventsService queueEventsService = new QueueEventsService();
  private final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  @Test
  void testGenerateEvents() {
    var testInput =
        makeQueueDetailResponse(
            List.of(
                makeToken(2, "2011-01-01 10:00:00"),
                makeRemovedToken(1, "2011-01-01 08:00:00", "2011-01-01 11:00:00")));
    var events = queueEventsService.getQueueEvents(testInput);

    // Since there was one created and one deleted token, there should be 1 + 2 = 3 events.
    Assertions.assertEquals(3, events.getEvents().size());

    // Assert that events in order reflects token 1 removal, then token 2 creation and finally
    // token 1 creation.
    var event1 = events.getEvents().get(0);
    Assertions.assertEquals(EventType.TOKEN_REMOVED, event1.getEventType());
    Assertions.assertEquals(1, event1.getTokenNumber());
    Assertions.assertNotNull(event1.getEventTimestamp());

    var event2 = events.getEvents().get(1);
    Assertions.assertEquals(EventType.TOKEN_ADDED, event2.getEventType());
    Assertions.assertEquals(2, event2.getTokenNumber());
    Assertions.assertNotNull(event2.getEventTimestamp());

    var event3 = events.getEvents().get(2);
    Assertions.assertEquals(EventType.TOKEN_ADDED, event3.getEventType());
    Assertions.assertEquals(1, event3.getTokenNumber());
    Assertions.assertNotNull(event3.getEventTimestamp());
  }

  @SneakyThrows
  private Token makeToken(Integer tokenNumber, String creationTimestamp) {
    var token = new Token();

    token.setTokenNumber(tokenNumber);
    token.setTokenCreationTimestamp(parser.parse(creationTimestamp));
    token.setStatus(TokenStatus.WAITING);

    return token;
  }

  @SneakyThrows
  private Token makeRemovedToken(
      Integer tokenNumber, String creationTimestamp, String deletionTimeStamp) {
    var token = new Token();

    token.setTokenNumber(tokenNumber);
    token.setTokenCreationTimestamp(parser.parse(creationTimestamp));
    token.setTokenDeletionTimestamp(parser.parse(deletionTimeStamp));
    token.setStatus(TokenStatus.REMOVED);

    return token;
  }

  private QueueDetailsResponse makeQueueDetailResponse(List<Token> tokenList) {
    var response = new QueueDetailsResponse(null, null, null, null, null, null, false);
    tokenList.forEach(response::addToken);
    return response;
  }
}
