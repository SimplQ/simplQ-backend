package me.simplq.controller.advices;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import me.simplq.exceptions.SQAccessDeniedException;
import org.junit.jupiter.api.Test;

class AuthenticationFilterTest {
  private static final String keyUrl = "https://simplq.us.auth0.com/.well-known/jwks.json";

  @Test
  void denyBadHeaderValues() throws MalformedURLException {
    var authFilter = new AuthenticationFilter(new LoggedInUserInfo(), keyUrl);
    // Deny Null
    assertThrows(SQAccessDeniedException.class, () -> authFilter.authenticate(null));

    // Deny Empty String
    assertThrows(SQAccessDeniedException.class, () -> authFilter.authenticate(""));

    // Deny Malformed String
    assertThrows(SQAccessDeniedException.class, () -> authFilter.authenticate("invalid header"));
    assertThrows(SQAccessDeniedException.class, () -> authFilter.authenticate("invalid-header"));

    // Deny bad bearer token
    assertThrows(
        SQAccessDeniedException.class, () -> authFilter.authenticate("Bearer invalid-header"));
  }

  @Test
  void setAnonymousUserIdentity() throws MalformedURLException {
    var loggedInUserInfo = new LoggedInUserInfo();
    var authFilter = new AuthenticationFilter(loggedInUserInfo, keyUrl);
    authFilter.authenticate("Anonymous anonymous-test-id");
    assertEquals("anonymous-test-id", loggedInUserInfo.getUserId());

    // Check backward compatibility, should be removed once frontend is updated on main site
    assertDoesNotThrow(() -> authFilter.authenticate("Bearer anonymous"));
  }
}
