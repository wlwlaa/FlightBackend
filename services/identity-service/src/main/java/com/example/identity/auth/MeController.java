package com.example.identity.auth;

import com.example.identity.common.ApiException;
import com.example.identity.security.JwtAuthFilter;
import com.example.identity.user.UserEntity;
import com.example.identity.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class MeController {

  private final UserRepository users;

  public MeController(UserRepository users) {
    this.users = users;
  }

  @GetMapping("/v1/me")
  public MeResponse me(Authentication auth) {
    if (auth == null || !(auth.getPrincipal() instanceof JwtAuthFilter.ActorPrincipal p)) {
      throw new ApiException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
    if ("guest".equalsIgnoreCase(p.actor())) {
      return new MeResponse("guest", p.subject(), null, null);
    }

    UserEntity u = users.findById(p.userId())
        .orElseThrow(() -> new ApiException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Unauthorized"));
    return new MeResponse("user", u.getId().toString(), u.getEmail(), u.getCreatedAt());
  }

  public record MeResponse(String mode, String id, String email, Instant createdAt) {}
}
