package me.simplq.controller.advices;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import me.simplq.exceptions.SQAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Order(1)
@Profile("!test")
public class AuthenticationFilter implements Filter {

  private static final String UNAUTHORIZED = "Unauthorized";
  private static final String INVALID_TOKEN = "Invalid auth token";
  private final GoogleIdTokenVerifier verifier;
  private final LoggedInUserInfo loggedInUserInfo;
  private final JwkProvider provider;

  // TODO Remove google stuff
  @Autowired
  AuthenticationFilter(
      LoggedInUserInfo loggedInUserInfo,
      @Value("#{'${google.auth.clientIds}'.split(',')}") List<String> clientIds,
      @Value("${auth0.jkws.url}") String keyUrl)
      throws MalformedURLException {
    this.loggedInUserInfo = loggedInUserInfo;
    verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(clientIds)
            .build();
    provider = new UrlJwkProvider(new URL(keyUrl));
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
    try {
      var jwt = JWT.decode(token);
      var jwk = provider.get(jwt.getKeyId());
      var algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
      algorithm.verify(jwt);
      loggedInUserInfo.setUserId(jwt.getClaim("sub").asString());
    } catch (SignatureVerificationException | JwkException ex) {
      // At this point we should return unauthorized. Checking google tokens for temporary
      // compatibility.
      //      throw new SQAccessDeniedException("Invalid authorization token");

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
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    authenticate(((HttpServletRequest) request).getHeader("Authorization"));
    chain.doFilter(request, response);
  }
}
