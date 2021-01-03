package me.simplq.service;

import lombok.RequiredArgsConstructor;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.dao.Owner;
import me.simplq.dao.OwnerRepository;
import me.simplq.exceptions.SQAccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {
  private static final String ACCESS_DENIED_ERROR_TEXT = "Please sign in to use this feature";

  private final OwnerRepository ownerRepository;
  private final LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public Owner getOwnerOrElseCreate(String ownerId) {
    return ownerRepository.findById(ownerId).orElse(ownerRepository.save(new Owner(ownerId)));
  }

  @Transactional
  public Boolean isDeviceLinked(String deviceId) {
    failIfAnonymous();
    return deviceId.equals(
        ownerRepository
            .findById(loggedInUserInfo.getUserId())
            .orElse(Owner.empty())
            .getCompanionDevice());
  }

  @Transactional
  public void linkDevice(String deviceId) {
    failIfAnonymous();
    ownerRepository
        .findById(loggedInUserInfo.getUserId())
        .orElseThrow()
        .setCompanionDevice(deviceId);
  }

  private void failIfAnonymous() {
    if (loggedInUserInfo.isAnonymous()) {
      throw new SQAccessDeniedException(ACCESS_DENIED_ERROR_TEXT);
    }
  }
}
