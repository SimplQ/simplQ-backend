package com.example.restservice.service;

import com.example.restservice.constants.UserStatus;
import com.example.restservice.dao.UserRepository;
import com.example.restservice.exceptions.SQInternalServerException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import com.example.restservice.model.DeleteUserRequest;
import com.example.restservice.model.UserStatusRequest;
import com.example.restservice.model.UserStatusResponse;
import com.example.restservice.service.smsService.SmsManager;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;
  @Autowired private SmsManager smsManager;

  public UserStatusResponse getStatus(String tokenId) {
    return new UserStatusResponse(
        tokenId,
        userRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::userNotFoundException)
            .getStatus(),
        getAheadCount(tokenId).orElseThrow(SQInternalServerException::new));
  }

  @Transactional
  public void deleteUserFromQueue(DeleteUserRequest deleteUserRequest) {
    userRepository.setUserStatusById(UserStatus.REMOVED, deleteUserRequest.getTokenId());
  }

  /** Notify user on User page. Send SMS notification */
  @Transactional
  public void alertUser(UserStatusRequest userStatusRequest) {
    var user =
        userRepository
            .findById(userStatusRequest.getTokenId())
            .orElseThrow(SQInvalidRequestException::userNotFoundException);
    if (user.getStatus() == UserStatus.WAITING) {
      smsManager.notify(user.getContactNumber(), user.getQueue().getQueueName());
    }
    userRepository.setUserStatusById(UserStatus.NOTIFIED, userStatusRequest.getTokenId());
  }

  public Optional<Long> getAheadCount(String tokenId) {
    var user =
        userRepository
            .findById(tokenId)
            .orElseThrow(SQInvalidRequestException::userNotFoundException);

    if (user.getStatus() == UserStatus.REMOVED) {
      return Optional.empty();
    }

    var aheadCount =
        Optional.of(
            user.getQueue().getUsers().stream()
                .filter(
                    fellowUser ->
                        fellowUser.getTimestamp().before(user.getTimestamp())
                            && !fellowUser.getStatus().equals(UserStatus.REMOVED))
                .count());
    return aheadCount;
  }
}
