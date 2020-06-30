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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  @Autowired private TokenRepository tokenRepository;
  @Autowired private QueueRepository queueRepository;
  @Autowired private SmsManager smsManager;
  @Autowired private LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public TokenDetailResponse getToken(String tokenId) {
    var token =
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException);
    return new TokenDetailResponse(
        tokenId,
        token.getStatus(),
        token.getQueue().getQueueName(),
        getAheadCount(tokenId).orElseThrow(SQInternalServerException::new));
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
    var user =
        queueRepository
            .findById(createTokenRequest.getQueueId())
            .map(
                queue -> {
                  var newUser =
                      new Token(
                          createTokenRequest.getName(),
                          createTokenRequest.getContactNumber(),
                          TokenStatus.WAITING,
                          ObjectUtils.defaultIfNull(createTokenRequest.getNotifyable(), false),
                          loggedInUserInfo.getUserId());
                  newUser.setQueue(queue);
                  tokenRepository.save(newUser);
                  return newUser;
                })
            .orElseThrow(SQInvalidRequestException::queueNotFoundException);
    return new TokenDetailResponse(
        user.getTokenId(),
        user.getStatus(),
        user.getQueue().getQueueName(),
        getAheadCount(user.getTokenId()).orElseThrow(SQInternalServerException::new));
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

  private Optional<Long> getAheadCount(String tokenId) {
    var user =
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException);

    if (user.getStatus() == TokenStatus.REMOVED) {
      return Optional.empty();
    }

    var aheadCount =
        Optional.of(
            user.getQueue().getTokens().stream()
                .filter(
                    fellowUser ->
                        fellowUser.getTimestamp().before(user.getTimestamp())
                            && !fellowUser.getStatus().equals(TokenStatus.REMOVED))
                .count());
    return aheadCount;
  }
}
