package me.simplq.controller.advices;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import me.simplq.exceptions.SQAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {

  private static final String UNAUTHORIZED = "Unauthorized";
  private static final String INVALID_TOKEN = "Invalid auth token";
  private final GoogleIdTokenVerifier verifier;
  private final LoggedInUserInfo loggedInUserInfo;

  @Autowired
  AuthenticationFilter(
      LoggedInUserInfo loggedInUserInfo,
      @Value("#{'${google.auth.clientIds}'.split(',')}") List<String> clientIds) {
    this.loggedInUserInfo = loggedInUserInfo;
    verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(clientIds)
            .build();
  }

  private void authenticate(String authHeaderVal) {
    if (StringUtils.isEmpty(authHeaderVal)) {
      throw new SQAccessDeniedException(UNAUTHORIZED);
    }
    var token = authHeaderVal.replaceFirst("^Bearer ", "");
    if (StringUtils.isEmpty(token)) {
      throw new SQAccessDeniedException(UNAUTHORIZED);
    }
    if (token.equals("anonymous")) {
      loggedInUserInfo.setUserId(token);
      return;
    }
    GoogleIdToken idToken = null;
    try {
      idToken = verifier.verify(token);
    } catch (GeneralSecurityException e) {
      throw new SQAccessDeniedException(UNAUTHORIZED);
    } catch (IOException e) {
      throw new SQAccessDeniedException(INVALID_TOKEN);
    }
    if (idToken == null) {
      throw new SQAccessDeniedException(INVALID_TOKEN);
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
