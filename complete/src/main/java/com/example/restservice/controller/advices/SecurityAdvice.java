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
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

@ControllerAdvice
public class SecurityAdvice extends RequestBodyAdviceAdapter {

  private final JwkProvider provider;
  private final LoggedInUserInfo loggedInUserInfo;

  @Autowired
  SecurityAdvice(LoggedInUserInfo loggedInUserInfo) throws MalformedURLException {
    this.loggedInUserInfo = loggedInUserInfo;
    String keyUrl = "https://cognito-idp.ap-southeast-1.amazonaws.com/ap-southeast-1_iQdl5AVrA/.well-known/jwks.json";
    provider = new UrlJwkProvider(new URL(keyUrl));
  }

  void validate(DecodedJWT jwt) {
    try {
      var jwk = provider.get(jwt.getKeyId());
      var algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
      algorithm.verify(jwt);
    } catch (SignatureVerificationException | JwkException e) {
      throw new SQAccessDeniedException("Invalid authorization token");
    }
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    DecodedJWT jwt;
    try {
      jwt = JWT.decode(inputMessage.getHeaders().get("Authorization").get(0)
          .replaceFirst("^Bearer ", ""));
    } catch (NullPointerException | JWTDecodeException e) {
      throw new SQAccessDeniedException("Invalid authorization header");
    }
    validate(jwt);
    loggedInUserInfo.setUserId(jwt.getClaim("username").asString());
    return inputMessage;
  }

  @Override
  public boolean supports(MethodParameter methodParameter, Type type,
      Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }
}
