package me.simplq.service.notification;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.service.SecretsManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("text-local")
public class TexLocalSmsService implements NotificationChannel {
  private final String API_KEY;
  private final String SENDER_NAME = "TXTLCL";
  private final String TEXT_LOCAL_API = "https://api.textlocal.in/send/?";

  public TexLocalSmsService(SecretsManager secretsManager) {
    API_KEY = secretsManager.getSecret("TEXT_LOCAL_API_KEY");
  }

  @Override
  public void notify(Token token, String payload) {
    postSmsRequest(constructData(token.getContactNumber(), payload));
  }

  private void postSmsRequest(String data) {
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(TEXT_LOCAL_API).openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
      conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));

      final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      final StringBuffer stringBuffer = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        stringBuffer.append(line);
      }
      rd.close();
    } catch (Exception e) {
      // TODO Move to exception framework
      throw new SQInternalServerException("Error sending SMS" + e.getMessage(), e);
    }
  }

  private String constructData(String contactNumber, String payload) {
    String user = "apikey=" + API_KEY;
    String message = "&message=" + payload;
    String sender = "&sender=" + SENDER_NAME;
    String numbers = "&numbers=" + contactNumber;
    return user + numbers + message + sender;
  }
}
