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

  private static final String START_MESSAGE_EXPECTED =
      "Hi test-name,\n"
          + "You have been added to test-queue. Your token number is 42. You can know your status"
          + " visiting http://token-test-url/test-token-id\n"
          + "Thanks for using simplq.me";

  private static final String END_MESSAGE_EXPECTED =
      "Hi, your wait for test-queue is over! You can proceed";

  @Autowired private MessagesManager manager;

  @Test
  void endWaiting() {
    Message message = manager.endWaiting("test-queue");

    assertThat(message).isInstanceOf(EndWaitingMessage.class);
    assertThat(message).isNotNull();
    assertThat(message.text()).isEqualTo(END_MESSAGE_EXPECTED);
  }

  @Test
  void startWaiting() {
    Message message = manager.startWaiting("test-name", "test-queue", 42, "test-token-id");

    assertThat(message).isInstanceOf(StartWaitingMessage.class);
    assertThat(message).isNotNull();
    assertThat(message.text()).isEqualTo(START_MESSAGE_EXPECTED);
  }
}
