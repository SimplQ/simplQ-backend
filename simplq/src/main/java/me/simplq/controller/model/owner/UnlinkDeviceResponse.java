package me.simplq.controller.model.owner;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UnlinkDeviceResponse {

  final String deviceId;
}
