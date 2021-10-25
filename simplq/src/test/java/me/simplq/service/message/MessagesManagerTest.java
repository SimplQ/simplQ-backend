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
          + "\n"
          + "You have been added to queue test-queue. Your token number is 42.\n"
          + "\n"
          + "You can check your live status by visiting http://token-test-url/test-token-id\n"
          + "\n"
          + "Please wait to be notified before you visit the location. Stay away from crowds and"
          + " have a delightful experience.\n"
          + "\n"
          + "Thanks for using simplq.me, a free and open source queue management software.\n"
          + "\n"
          + "Regards,\n"
          + "Team SimplQ\n"
          + "https://www.simplq.me/";

  private static final String END_MESSAGE_SUBJECT_EXPECTED =
      "test-queue: Hooray! your wait is finally over.";
  private static final String END_MESSAGE_BODY_EXPECTED =
      "Hi test-queue,\n"
          + "\n"
          + "You have been notified by the queue admin. Your turn will be up soon.\n"
          + "\n"
          + "Please proceed to the location now.\n"
          + "\n"
          + "Thanks for using simplq.me, a free and open source queue management software.\n"
          + "\n"
          + "Regards,\n"
          + "Team SimplQ\n"
          + "https://www.simplq.me/";

  @Autowired private MessagesManager manager;

  @Test
  void endWaiting() {
    Message message = manager.endWaiting("test-queue", "test-user-name");

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
