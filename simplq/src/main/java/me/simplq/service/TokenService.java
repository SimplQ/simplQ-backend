package me.simplq.service;

import java.util.Comparator;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.controller.model.token.CreateTokenRequest;
import me.simplq.controller.model.token.MyTokensResponse;
import me.simplq.controller.model.token.TokenDeleteResponse;
import me.simplq.controller.model.token.TokenDetailResponse;
import me.simplq.controller.model.token.TokenNotifyResponse;
import me.simplq.dao.QueueRepository;
import me.simplq.dao.Token;
import me.simplq.dao.TokenRepository;
import me.simplq.exceptions.SQInternalServerException;
import me.simplq.exceptions.SQInvalidRequestException;
import me.simplq.service.smsService.SmsManager;

@Service
public class TokenService {

  private static final String TOKEN_CREATION_MESSAGE =
      "Hi %s,\n"
          + "You have been added to %s. Your token number is %d. You can know your status visiting"
          + " %s\n"
          + "Thanks for using simplq.me";
  private static final String TOKEN_NOTIFICATION_MESSAGE =
      "Hi, your wait for %s is over! You can proceed";
  
  private final TokenRepository tokenRepository;
  private final QueueRepository queueRepository;
  private final SmsManager smsManager;
  private final LoggedInUserInfo loggedInUserInfo;

  @Autowired
  public TokenService(TokenRepository tokenRepository, QueueRepository queueRepository,
        SmsManager smsManager, LoggedInUserInfo loggedInUserInfo) {

          this.tokenRepository = tokenRepository;
          this.queueRepository = queueRepository;
          this.smsManager = smsManager;
          this.loggedInUserInfo = loggedInUserInfo;
    
  }

  @Transactional
  public TokenDetailResponse getToken(String tokenId) {
    var token =
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException);
    return new TokenDetailResponse(
        tokenId,
        token.getName(),
        token.getContactNumber(),
        token.getTokenNumber(),
        token.getStatus(),
        token.getQueue().getQueueName(),
        token.getQueue().getQueueId(),
        getAheadCount(token),
        token.getNotifiable(),
        token.getTokenCreationTimestamp());
  }

  @Transactional
  public TokenDeleteResponse deleteToken(String tokenId) {
    tokenRepository.setTokenStatusById(TokenStatus.REMOVED, tokenId);
    return tokenRepository
        .findById(tokenId)
        .map(
            token ->
                new TokenDeleteResponse(
                    tokenId, token.getQueue().getQueueName(), token.getStatus()))
        .orElseThrow(SQInternalServerException::new);
  }

  /** Notify user on User page. Send SMS notification */
  @Transactional
  public TokenNotifyResponse notifyToken(String tokenId) {
    var user =
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException);
    if (user.getStatus() == TokenStatus.WAITING) {
      smsManager.notify(
          user.getContactNumber(),
          String.format(TOKEN_NOTIFICATION_MESSAGE, user.getQueue().getQueueName()));
    } else {
      throw SQInvalidRequestException.tokenNotNotifiableException();
    }
    tokenRepository.setTokenStatusById(TokenStatus.NOTIFIED, tokenId);
    return new TokenNotifyResponse(tokenId, TokenStatus.NOTIFIED);
  }

  @Transactional
  public TokenDetailResponse createToken(CreateTokenRequest createTokenRequest) {
    var token =
        queueRepository
            .findById(createTokenRequest.getQueueId())
            .map(
                queue -> {
                  if (queue.getStatus().equals(QueueStatus.PAUSED)) {
                    throw SQInvalidRequestException.queuePausedException();
                  } else if (queue.getStatus().equals(QueueStatus.DELETED)) {
                    throw SQInvalidRequestException.queueDeletedException();
                  } else if (queue.isFull()) {
                    throw SQInvalidRequestException.queueIsFullException();
                  }
                  var newToken =
                      new Token(
                          createTokenRequest.getName(),
                          createTokenRequest.getContactNumber(),
                          TokenStatus.WAITING,
                          ObjectUtils.defaultIfNull(createTokenRequest.getNotifiable(), false),
                          loggedInUserInfo.getUserId());
                  newToken.setQueue(queue);
                  tokenRepository.save(newToken);
                  return newToken;
                })
            .orElseThrow(SQInvalidRequestException::queueNotFoundException);
    var currentMaxTokenNumber =
        tokenRepository.getLastTokenNumberForQueue(token.getQueue().getQueueId());
    var nextTokenNumber = currentMaxTokenNumber != null ? currentMaxTokenNumber + 1 : 1;
    token.setTokenNumber(nextTokenNumber);
    smsManager.notify(
        token.getContactNumber(),
        String.format(
            TOKEN_CREATION_MESSAGE,
            token.getName(),
            token.getQueue().getQueueName(),
            token.getTokenNumber(),
            token.getTokenUrl()));
    return new TokenDetailResponse(
        token.getTokenId(),
        token.getName(),
        token.getContactNumber(),
        token.getTokenNumber(),
        token.getStatus(),
        token.getQueue().getQueueName(),
        token.getQueue().getQueueId(),
        getAheadCount(token),
        token.getNotifiable(),
        token.getTokenCreationTimestamp());
  }

  @Transactional
  public MyTokensResponse getMyTokens() {
    return new MyTokensResponse(
        tokenRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .sorted(
                new Comparator<Token>() {
                  public int compare(Token a, Token b) {
                    return a.getTokenCreationTimestamp().compareTo(b.getTokenCreationTimestamp());
                  }
                })
            .map(
                token ->
                    new MyTokensResponse.Token(
                        token.getQueue().getQueueName(),
                        token.getTokenId(),
                        token.getTokenCreationTimestamp()))
            .collect(Collectors.toList()));
  }

  private Long getAheadCount(Token token) {
    if (token.getStatus() == TokenStatus.REMOVED) {
      return null;
    }
    return token.getQueue().getTokens().stream()
        .filter(
            fellowUser ->
                fellowUser.getTokenCreationTimestamp().before(token.getTokenCreationTimestamp())
                    && !fellowUser.getStatus().equals(TokenStatus.REMOVED))
        .count();
  }
}
