package com.example.restservice.controller;

import com.example.restservice.controller.model.token.CreateTokenRequest;
import com.example.restservice.controller.model.token.MyTokensResponse;
import com.example.restservice.controller.model.token.TokenDeleteResponse;
import com.example.restservice.controller.model.token.TokenDetailResponse;
import com.example.restservice.controller.model.token.TokenNotifyResponse;
import com.example.restservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  @GetMapping(path = "/token/{tokenId}")
  public ResponseEntity<TokenDetailResponse> getToken(@PathVariable("tokenId") String tokenId) {
    return ResponseEntity.ok(tokenService.getToken(tokenId));
  }

  @PostMapping(path = "/token")
  public ResponseEntity<TokenDetailResponse> createToken(
      @RequestBody CreateTokenRequest createTokenRequest) {
    return ResponseEntity.ok(tokenService.createToken(createTokenRequest));
  }

  @GetMapping(path = "/tokens")
  public ResponseEntity<MyTokensResponse> getMyTokens() {
    return ResponseEntity.ok(tokenService.getMyTokens());
  }

  @DeleteMapping(path = "/token/{tokenId}")
  public ResponseEntity<TokenDeleteResponse> deleteToken(@PathVariable("tokenId") String tokenId) {
    return ResponseEntity.ok(tokenService.deleteToken(tokenId));
  }

  @PutMapping(path = "/token/notify/{tokenId}")
  public ResponseEntity<TokenNotifyResponse> notifyToken(@PathVariable("tokenId") String tokenId) {
    return ResponseEntity.ok(tokenService.notifyToken(tokenId));
  }
}
