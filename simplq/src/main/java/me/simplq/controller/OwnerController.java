package me.simplq.controller;

import lombok.RequiredArgsConstructor;
import me.simplq.controller.model.owner.LinkDeviceResponse;
import me.simplq.controller.model.owner.UnlinkDeviceResponse;
import me.simplq.service.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/owner")
@RequiredArgsConstructor
public class OwnerController {
  private final OwnerService ownerService;

  @PutMapping(path = "/link")
  public ResponseEntity<LinkDeviceResponse> linkDevice(@RequestParam String deviceId) {
    ownerService.linkDevice(deviceId);
    return ResponseEntity.ok(new LinkDeviceResponse(deviceId));
  }

  @PatchMapping(path = "/unlink")
  public ResponseEntity<UnlinkDeviceResponse> unlinkDevice(@RequestParam String deviceId) {
    ownerService.unlinkDevice(deviceId);
    return ResponseEntity.ok(new UnlinkDeviceResponse(deviceId));
  }
}
