package me.simplq.service;

import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.dao.Device;
import me.simplq.dao.DeviceRepository;
import me.simplq.dao.Owner;
import me.simplq.dao.OwnerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
  private static final String ACCESS_DENIED_ERROR_TEXT = "Please sign in to use this feature";

  private final OwnerRepository ownerRepository;
  private final DeviceRepository deviceRepository;
  private final LoggedInUserInfo loggedInUserInfo;

  @Transactional
  public Owner getOwnerOrElseCreate() {
    return getOwnerOrElseCreateInternal();
  }

  @Transactional
  public Stream<Device> getDevices(String ownerId) {
    return deviceRepository.findByOwnerId(ownerId);
  }

  @Transactional
  public void linkDevice(String deviceId) {
    var owner = getOwnerOrElseCreateInternal();
    deviceRepository
        .findById(deviceId)
        .ifPresentOrElse(
            device -> device.setOwner(owner),
            () -> deviceRepository.save(new Device(deviceId, owner)));
  }

  @Transactional
  public void unlinkDevice(String deviceId) {
    var owner = getOwnerOrElseCreateInternal();
    deviceRepository
        .findById(deviceId)
        .filter(device -> device.getOwner().getId().equals(owner.getId()))
        .ifPresent(deviceRepository::delete);
  }

  private Owner getOwnerOrElseCreateInternal() {
    var ownerId = loggedInUserInfo.getUserId();
    return ownerRepository
        .findById(ownerId)
        .orElseGet(() -> ownerRepository.save(new Owner(ownerId)));
  }
}
