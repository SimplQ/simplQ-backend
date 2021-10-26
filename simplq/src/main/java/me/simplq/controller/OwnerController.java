package me.simplq.controller;

import lombok.RequiredArgsConstructor;
import me.simplq.controller.model.owner.LinkDeviceResponse;
import me.simplq.controller.model.owner.UnlinkDeviceResponse;
import me.simplq.service.OwnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/owner")
@RequiredArgsConstructor
public class OwnerController {
  private final OwnerService ownerService;

  @PutMapping(path = "/link")
  public LinkDeviceResponse linkDevice(@RequestParam() String deviceId) {
    ownerService.linkDevice(deviceId);
    return new LinkDeviceResponse(deviceId);
  }

  @PatchMapping(path = "/unlink")
  public UnlinkDeviceResponse unlinkDevice(@RequestParam() String deviceId) {
    ownerService.unlinkDevice(deviceId);
    return new UnlinkDeviceResponse(deviceId);
  }
}
