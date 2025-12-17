package com.example.identity.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.identity.auth.AuthDtos.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  private final AuthService auth;
  private final com.example.identity.security.JwtService jwt;

  public AuthController(AuthService auth, com.example.identity.security.JwtService jwt) {
    this.auth = auth;
    this.jwt = jwt;
  }

  @PostMapping("/login")
  public AuthTokensResponse login(@Valid @RequestBody LoginRequest req) {
    return auth.login(req);
  }

  @PostMapping("/refresh")
  public AuthTokensResponse refresh(@Valid @RequestBody RefreshRequest req) {
    return auth.refresh(req);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req) {
    auth.logout(req);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/guest")
  public GuestAuthResponse guest(@Valid @RequestBody GuestAuthRequest req) {
    String access = jwt.mintGuestAccessToken(req.deviceId());
    return new GuestAuthResponse(access, jwt.getAccessTtlSeconds(), "Bearer");
  }
}
