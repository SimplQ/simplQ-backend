package me.simplq.controller.advices;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthenticationFilter implements Filter {

  private static final String UNAUTHORIZED = "Unauthorized";
  private static final String INVALID_TOKEN = "Invalid auth token";
  private final LoggedInUserInfo loggedInUserInfo;
  private final JwkProvider provider;

  // TODO Remove google stuff
  @Autowired
  AuthenticationFilter(LoggedInUserInfo loggedInUserInfo, @Value("${auth0.jkws.url}") String keyUrl)
      throws MalformedURLException {
    this.loggedInUserInfo = loggedInUserInfo;
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
      log.error("Auth0 authentication failed", ex);
      throw new SQAccessDeniedException(UNAUTHORIZED);
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    authenticate(((HttpServletRequest) request).getHeader("Authorization"));
    chain.doFilter(request, response);
  }
}
