package com.example.restservice.controller.advices;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.restservice.exceptions.SQAccessDeniedException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {

  private final JwkProvider provider;
  private final LoggedInUserInfo loggedInUserInfo;

  @Autowired
  AuthenticationFilter(
      LoggedInUserInfo loggedInUserInfo, @Value("${cognito.jkws.url}") String keyUrl)
      throws MalformedURLException {
    this.loggedInUserInfo = loggedInUserInfo;
    provider = new UrlJwkProvider(new URL(keyUrl));
  }

  private void validate(DecodedJWT jwt) {
    try {
      var jwk = provider.get(jwt.getKeyId());
      var algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
      algorithm.verify(jwt);
    } catch (SignatureVerificationException | JwkException e) {
      throw new SQAccessDeniedException("Invalid authorization token");
    }
  }

  private void authenticate(String authHeaderVal) {
    DecodedJWT jwt;
    try {
      jwt = JWT.decode(authHeaderVal.replaceFirst("^Bearer ", ""));
    } catch (IllegalArgumentException | NullPointerException | JWTDecodeException e) {
      throw new SQAccessDeniedException("Invalid authorization header");
    }
    validate(jwt);
    loggedInUserInfo.setUserId(jwt.getClaim("username").asString());
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    authenticate(((HttpServletRequest) request).getHeader("Authorization"));
    chain.doFilter(request, response);
  }
}
