package com.example.identity.security;

import com.example.identity.common.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring("Bearer ".length()).trim();
      try {
        JwtService.VerifiedJwt v = jwtService.verifyAccessToken(token);
        UUID userId = v.userIdOrNull();
        var principal = new ActorPrincipal(v.subject(), v.actor(), userId, v.email());

        String role = v.isGuest() ? "ROLE_GUEST" : "ROLE_USER";
        var authentication = new UsernamePasswordAuthenticationToken(
            principal,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        throw new ApiException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Invalid or expired token");
      }
    }

    filterChain.doFilter(request, response);
  }

  public record ActorPrincipal(String subject, String actor, UUID userId, String email) {}
}
