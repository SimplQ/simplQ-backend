package me.simplq.service.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagesManager.class)
@TestPropertySource(properties = {"token.url=http://token-test-url/"})
class MessagesManagerTest {

  private static final String START_MESSAGE_SUBJECT_EXPECTED =
      "test-queue: You have been added to the queue.";
  private static final String START_MESSAGE_BODY_EXPECTED =
      "Hi test-name,\n"
          + "You have been added to test-queue. Your token number is 42.\n"
          + "You can check your live status by visiting http://token-test-url/test-token-id\n"
          + "\n"
          + "Thanks for using simplq.me, a free and open source queue management software.";

  private static final String END_MESSAGE_SUBJECT_EXPECTED =
      "test-queue: Hooray! your wait is finally over.";
  private static final String END_MESSAGE_BODY_EXPECTED =
      "You have been notified by the queue manager.";

  @Autowired private MessagesManager manager;

  @Test
  void endWaiting() {
    Message message = manager.endWaiting("test-queue");

    assertThat(message).isInstanceOf(EndWaitingMessage.class);
    assertThat(message).isNotNull();
    assertThat(message.subject()).isEqualTo(END_MESSAGE_SUBJECT_EXPECTED);
    assertThat(message.body()).isEqualTo(END_MESSAGE_BODY_EXPECTED);
  }

  @Test
  void startWaiting() {
    Message message = manager.startWaiting("test-name", "test-queue", 42, "test-token-id");

    assertThat(message).isInstanceOf(StartWaitingMessage.class);
    assertThat(message).isNotNull();
    assertThat(message.subject()).isEqualTo(START_MESSAGE_SUBJECT_EXPECTED);
    assertThat(message.body()).isEqualTo(START_MESSAGE_BODY_EXPECTED);
  }
}
