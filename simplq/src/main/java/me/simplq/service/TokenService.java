package me.simplq.service;

import java.util.Comparator;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import me.simplq.service.message.MessagesManager;
import me.simplq.service.smsService.SmsManager;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenRepository tokenRepository;
  private final QueueRepository queueRepository;
  private final OwnerService ownerService;
  private final SmsManager smsManager;
  private final LoggedInUserInfo loggedInUserInfo;
  private final MessagesManager messagesManager;

  @Transactional
  public TokenDetailResponse getToken(String tokenId) {
    return TokenDetailResponse.fromEntity(
        tokenRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException));
  }

  /** Get token by queueId and the contact number */
  @Transactional
  public TokenDetailResponse getToken(String queueId, String contactNumber) {
    return TokenDetailResponse.fromEntity(
        tokenRepository
            .findByQueueIdAndContactNumber(queueId, contactNumber)
            .orElseThrow(SQInvalidRequestException::tokenNotFoundException));
  }

  @Transactional
  public TokenDeleteResponse deleteToken(String tokenId) {
    return tokenRepository
        .findById(tokenId)
        .map(
            token -> {
              token.delete();
              tokenRepository.save(token);
              return new TokenDeleteResponse(
                  tokenId, token.getQueue().getQueueName(), token.getStatus());
            })
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
          messagesManager.endWaiting(user.getQueue().getQueueName()).text());
    } else {
      throw SQInvalidRequestException.tokenNotNotifiableException();
    }
    tokenRepository.setTokenStatusById(TokenStatus.NOTIFIED, tokenId);
    return new TokenNotifyResponse(tokenId, TokenStatus.NOTIFIED);
  }

  @Transactional
  public TokenDetailResponse createToken(CreateTokenRequest createTokenRequest) {
    if (tokenRepository.existsAlreadyInQueue(
        createTokenRequest.getQueueId(), createTokenRequest.getContactNumber())) {
      throw SQInvalidRequestException.tokenAlreadyWaiting();
    }
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
                  } else if (!queue.isSelfJoinAllowed()
                      && !loggedInUserInfo.getUserId().equals(queue.getOwner().getId())) {
                    throw SQInvalidRequestException.onlyOwnerCanCreateTokens();
                  }
                  var newToken =
                      new Token(
                          createTokenRequest.getName(),
                          createTokenRequest.getContactNumber(),
                          TokenStatus.WAITING,
                          ObjectUtils.defaultIfNull(createTokenRequest.getNotifiable(), false)
                              || StringUtils.isNotBlank(createTokenRequest.getEmailId()),
                          ownerService.getOwnerOrElseCreate().getId());
                  newToken.setQueue(queue);
                  newToken.setEmailId(createTokenRequest.getEmailId());
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
        messagesManager
            .startWaiting(
                token.getName(),
                token.getQueue().getQueueName(),
                token.getTokenNumber(),
                token.getTokenId())
            .text());
    return TokenDetailResponse.fromEntity(token);
  }

  @Transactional
  public MyTokensResponse getMyTokens() {
    return new MyTokensResponse(
        tokenRepository
            .findByOwnerId(loggedInUserInfo.getUserId())
            .sorted(Comparator.comparing(Token::getTokenCreationTimestamp))
            .map(
                token ->
                    new MyTokensResponse.Token(
                        token.getQueue().getQueueName(),
                        token.getTokenId(),
                        token.getTokenCreationTimestamp()))
            .collect(Collectors.toList()));
  }
}
