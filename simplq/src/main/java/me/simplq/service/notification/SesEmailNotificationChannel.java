package me.simplq.service.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInternalServerException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

@Slf4j
public class SesEmailNotificationChannel implements NotificationChannel {
  private final SesClient client = SesClient.builder().region(Region.US_WEST_2).build();
  private static final String FROM_EMAIL = "notifications@simplq.me";

  @Override
  public void notify(Token token, me.simplq.service.message.Message message) {
    log.info("Received request to send email notification for token {}", token.getTokenId());
    Optional.ofNullable(token.getEmailId())
        .ifPresent(emailId -> sendEmail(emailId, message, token.getQueue().getQueueName()));
  }

  private void sendEmail(
      String emailId, me.simplq.service.message.Message message, String subject) {
    log.info("Sending email to {}: {}", emailId, message.subject());
    try {
      Session session = Session.getDefaultInstance(new Properties());
      MimeMessage email = new MimeMessage(session);

      // Add subject, from and to lines
      email.setSubject(message.subject(), "UTF-8");
      email.setFrom(new InternetAddress(FROM_EMAIL));
      email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailId));

      // Create a multipart/alternative child container
      MimeMultipart msgBody = new MimeMultipart("alternative");

      // Create a wrapper for the HTML and text parts
      MimeBodyPart wrap = new MimeBodyPart();

      // Define the text part
      MimeBodyPart textPart = new MimeBodyPart();
      textPart.setContent(message.body(), "text/plain; charset=UTF-8");

      // Define the HTML part.
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(message.body(), "text/html; charset=UTF-8");

      // Add the text and HTML parts to the child container
      msgBody.addBodyPart(textPart);
      msgBody.addBodyPart(htmlPart);

      // Add the child container to the wrapper object
      wrap.setContent(msgBody);

      // Create a multipart/mixed parent container
      MimeMultipart msg = new MimeMultipart("mixed");

      // Add the parent container to the message
      email.setContent(msg);

      // Add the multipart/alternative part to the message
      msg.addBodyPart(wrap);

      System.out.println(
          "Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      email.writeTo(outputStream);
      ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

      byte[] arr = new byte[buf.remaining()];
      buf.get(arr);

      SdkBytes data = SdkBytes.fromByteArray(arr);
      RawMessage rawMessage = RawMessage.builder().data(data).build();

      SendRawEmailRequest rawEmailRequest =
          SendRawEmailRequest.builder().rawMessage(rawMessage).build();

      client.sendRawEmail(rawEmailRequest);

    } catch (SesException | MessagingException | IOException e) {
      throw new SQInternalServerException("Failed to send email", e);
    }
    log.info("Email sent to {}", emailId);
  }
}
