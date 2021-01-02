package me.simplq.controller;

import lombok.RequiredArgsConstructor;
import me.simplq.service.OwnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OwnerController {
  private final OwnerService ownerService;

  @GetMapping(path = "/me/status")
  @ResponseBody
  public Boolean isDeviceLinked(@RequestParam() String deviceId) {
    return ownerService.isDeviceLinked(deviceId);
  }

  @PutMapping(path = "/me/link")
  public void linkDevice(@RequestParam() String deviceId) {
    ownerService.linkDevice(deviceId);
  }
}
