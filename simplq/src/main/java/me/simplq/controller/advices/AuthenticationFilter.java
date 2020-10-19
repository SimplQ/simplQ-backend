package me.simplq.controller.advices;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import me.simplq.exceptions.SQAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {

  private final GoogleIdTokenVerifier verifier;
  private final LoggedInUserInfo loggedInUserInfo;

  @Autowired
  AuthenticationFilter(
      LoggedInUserInfo loggedInUserInfo, @Value("${google.auth.clientId}") String clientId) {
    this.loggedInUserInfo = loggedInUserInfo;
    verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(clientId))
            .build();
  }

  private void authenticate(String authHeaderVal) {
    var token = authHeaderVal.replaceFirst("^Bearer ", "");
    if (token.equals("anonymous")) {
      loggedInUserInfo.setUserId(token);
      return;
    }
    GoogleIdToken idToken = null;
    try {
      idToken = verifier.verify(token);
    } catch (GeneralSecurityException e) {
      throw new SQAccessDeniedException("Unauthorized");
    } catch (IOException e) {
      throw new SQAccessDeniedException("Invalid auth token");
    }
    if (idToken == null) {
      throw new SQAccessDeniedException("Invalid auth token");
    }
    loggedInUserInfo.setUserId(idToken.getPayload().getSubject());
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    authenticate(((HttpServletRequest) request).getHeader("Authorization"));
    chain.doFilter(request, response);
  }
}
