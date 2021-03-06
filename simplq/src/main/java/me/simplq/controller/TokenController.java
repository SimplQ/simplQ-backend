package me.simplq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.simplq.controller.model.token.CreateTokenRequest;
import me.simplq.controller.model.token.MyTokensResponse;
import me.simplq.controller.model.token.TokenDeleteResponse;
import me.simplq.controller.model.token.TokenDetailResponse;
import me.simplq.controller.model.token.TokenNotifyResponse;
import me.simplq.service.TokenService;

@RestController
@RequestMapping("/v1")
public class TokenController {

  private final TokenService tokenService;

  @Autowired
  public TokenController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

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
