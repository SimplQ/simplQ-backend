package com.example.restservice.service;

import com.example.restservice.constants.TokenStatus;
import com.example.restservice.controller.advices.LoggedInUserInfo;
import com.example.restservice.controller.model.token.CreateTokenRequest;
import com.example.restservice.controller.model.token.MyTokensResponse;
import com.example.restservice.controller.model.token.TokenDeleteResponse;
import com.example.restservice.controller.model.token.TokenDetailResponse;
import com.example.restservice.controller.model.token.TokenNotifyResponse;
import com.example.restservice.dao.QueueRepository;
import com.example.restservice.dao.Token;
import com.example.restservice.dao.TokenRepository;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import com.example.restservice.service.smsService.SmsManager;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenRepository tokenRepository;
  private final QueueRepository queueRepository;
  private final SmsManager smsManager;
  private final LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public TokenDetailResponse getToken(String tokenId) {
    var token =
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException);
    return new TokenDetailResponse(
        tokenId,
        token.getTokenNumber(),
        token.getStatus(),
        token.getQueue().getQueueName(),
        getAheadCount(token),
        token.getNotifiable());
  }

  @Transactional
  public TokenDeleteResponse deleteToken(String tokenId) {
    tokenRepository.setUserStatusById(TokenStatus.REMOVED, tokenId);
    return tokenRepository
        .findById(tokenId)
        .map(token -> new TokenDeleteResponse(token.getQueue().getQueueName(), token.getStatus()))
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
      smsManager.notify(user.getContactNumber(), user.getQueue().getQueueName());
    } else {
      throw SQInvalidRequestException.tokenNotNotifiableException();
    }
    tokenRepository.setUserStatusById(TokenStatus.NOTIFIED, tokenId);
    return new TokenNotifyResponse(tokenId, TokenStatus.NOTIFIED);
  }

  @Transactional
  public TokenDetailResponse createToken(CreateTokenRequest createTokenRequest) {
    var token =
        queueRepository
            .findById(createTokenRequest.getQueueId())
            .map(
                queue -> {
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
    return new TokenDetailResponse(
        token.getTokenId(),
        token.getTokenNumber(),
        token.getStatus(),
        token.getQueue().getQueueName(),
        getAheadCount(token),
        token.getNotifiable());
  }

  @Transactional
  public MyTokensResponse getMyTokens() {
    return new MyTokensResponse(
        tokenRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .map(
                token ->
                    new MyTokensResponse.Token(token.getQueue().getQueueName(), token.getTokenId()))
            .collect(Collectors.toList()));
  }

  private Long getAheadCount(Token token) {
    if (token.getStatus() == TokenStatus.REMOVED) {
      return null;
    }
    return token.getQueue().getTokens().stream()
        .filter(
            fellowUser ->
                fellowUser.getTimestamp().before(token.getTimestamp())
                    && !fellowUser.getStatus().equals(TokenStatus.REMOVED))
        .count();
  }
}
