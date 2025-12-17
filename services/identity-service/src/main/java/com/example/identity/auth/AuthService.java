package com.example.identity.auth;

import com.example.identity.common.ApiException;
import com.example.identity.security.JwtProperties;
import com.example.identity.security.JwtService;
import com.example.identity.user.RefreshTokenEntity;
import com.example.identity.user.RefreshTokenRepository;
import com.example.identity.user.UserEntity;
import com.example.identity.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static com.example.identity.auth.AuthDtos.*;

@Service
public class AuthService {

  private final UserRepository users;
  private final RefreshTokenRepository refreshTokens;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final JwtProperties jwtProps;

  public AuthService(UserRepository users,
                     RefreshTokenRepository refreshTokens,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService,
                     JwtProperties jwtProps) {
    this.users = users;
    this.refreshTokens = refreshTokens;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.jwtProps = jwtProps;
  }

  @Transactional
  public AuthTokensResponse login(LoginRequest req) {
    UserEntity u = users.findByEmail(req.email().toLowerCase())
        .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
      throw new ApiException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    return issueTokens(u);
  }

  @Transactional
  public AuthTokensResponse refresh(RefreshRequest req) {
    String hash = TokenHasher.sha256Base64(req.refreshToken());

    RefreshTokenEntity stored = refreshTokens.findFirstByTokenHash(hash)
        .orElseThrow(() -> new ApiException("INVALID_REFRESH", HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    if (!stored.isActive()) {
      throw new ApiException("INVALID_REFRESH", HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    UserEntity u = users.findById(stored.getUserId())
        .orElseThrow(() -> new ApiException("INVALID_REFRESH", HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    // rotate
    stored.revokeNow();
    refreshTokens.save(stored);

    return issueTokens(u);
  }

  @Transactional
  public void logout(LogoutRequest req) {
    String hash = TokenHasher.sha256Base64(req.refreshToken());
    refreshTokens.findFirstByTokenHash(hash).ifPresent(rt -> {
      rt.revokeNow();
      refreshTokens.save(rt);
    });
  }

  private AuthTokensResponse issueTokens(UserEntity u) {
    String access = jwtService.mintAccessToken(u.getId(), u.getEmail());

    String refreshPlain = "rft_" + UUID.randomUUID();
    String refreshHash = TokenHasher.sha256Base64(refreshPlain);

    Instant now = Instant.now();
    Instant exp = now.plus(jwtProps.refreshTtlDays(), ChronoUnit.DAYS);

    RefreshTokenEntity rt = new RefreshTokenEntity(
        UUID.randomUUID(),
        u.getId(),
        refreshHash,
        exp,
        null,
        now
    );
    refreshTokens.save(rt);

    return new AuthTokensResponse(access, refreshPlain, (int) jwtProps.accessTtlSeconds(), "Bearer");
  }
}
