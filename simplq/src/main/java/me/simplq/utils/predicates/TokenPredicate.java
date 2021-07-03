package me.simplq.utils.predicates;

import java.util.function.Predicate;
import me.simplq.constants.TokenStatus;
import me.simplq.dao.Token;
import me.simplq.exceptions.SQInvalidRequestException;

public class TokenPredicate {

  public Predicate<Token> isNotRemoved() {
    return token -> {
      if (TokenStatus.REMOVED.equals(token.getStatus())) {
        throw SQInvalidRequestException.tokenDeletedException();
      }

      return true;
    };
  }
}
