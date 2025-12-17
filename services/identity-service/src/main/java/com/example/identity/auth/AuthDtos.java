package com.example.identity.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

  public record LoginRequest(
      @Email @NotBlank String email,
      @NotBlank @Size(min = 6) String password
  ) {}

  public record RefreshRequest(
      @NotBlank String refreshToken
  ) {}

  public record LogoutRequest(
      @NotBlank String refreshToken
  ) {}

  public record GuestAuthRequest(
      @NotBlank String deviceId
  ) {}

  public record AuthTokensResponse(
      String accessToken,
      String refreshToken,
      int expiresIn,
      String tokenType
  ) {}

  public record GuestAuthResponse(
      String accessToken,
      int expiresIn,
      String tokenType
  ) {}

  private AuthDtos() {}
}
