package me.simplq.controller;

import lombok.RequiredArgsConstructor;
import me.simplq.service.OwnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/owner")
@RequiredArgsConstructor
public class OwnerController {
  private final OwnerService ownerService;

  @PutMapping(path = "/link")
  public void linkDevice(@RequestParam() String deviceId) {
    ownerService.linkDevice(deviceId);
  }

  @PatchMapping(path = "/unlink")
  public void unlinkDevice(@RequestParam() String deviceId) {
    ownerService.unlinkDevice(deviceId);
  }
}
