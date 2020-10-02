package me.simplq.service.smsService;

import static me.simplq.service.smsService.SmsConstants.SMS_MESSAGE;

import me.simplq.service.SecretsManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class TexLocalSmsService implements SmsService {
  private final Logger LOGGER = LoggerFactory.getLogger(TexLocalSmsService.class);
  private final String API_KEY;
  private final String SENDER_NAME = "TXTLCL";
  private final String TEXT_LOCAL_API = "https://api.textlocal.in/send/?";

  @Autowired
  public TexLocalSmsService(SecretsManager secretsManager) {
    API_KEY = secretsManager.getSecret("TEXT_LOCAL_API_KEY");
  }

  @Override
  public String sendSMS(String contactNumber, String queueName) {
    String data = constructData(contactNumber, queueName);
    return postSmsRequest(data);
  }

  private String postSmsRequest(String data) {
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(TEXT_LOCAL_API).openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
      conn.getOutputStream().write(data.getBytes("UTF-8"));

      final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      final StringBuffer stringBuffer = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        stringBuffer.append(line);
      }
      rd.close();
      return stringBuffer.toString();
    } catch (Exception e) {
      LOGGER.error("Error sending SMS", e);
      return "Error sending SMS" + e.getMessage();
    }
  }

  private String constructData(String contactNumber, String queueName) {
    String user = "apikey=" + API_KEY;
    String message = "&message=" + String.format(SMS_MESSAGE, queueName);
    String sender = "&sender=" + SENDER_NAME;
    String numbers = "&numbers=" + contactNumber;
    return user + numbers + message + sender;
  }
}
