package com.example.identity.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  private final JwtProperties props;
  private final byte[] secret;

  public JwtService(JwtProperties props) {
    this.props = props;
    this.secret = props.secret().getBytes(StandardCharsets.UTF_8);
    if (this.secret.length < 32) {
      throw new IllegalStateException("app.jwt.secret must be at least 32 chars");
    }
  }

  public String mintAccessToken(UUID userId, String email) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.accessTtlSeconds());

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(props.issuer())
        .subject(userId.toString())
        .claim("email", email)
        .claim("act", "user")
        .claim("typ", "access")
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(exp))
        .build();

    return sign(claims);
  }

  public String mintGuestAccessToken(String deviceId) {
    String guestId = "guest:" + base64UrlSha256(deviceId);

    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.accessTtlSeconds());

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(props.issuer())
        .subject(guestId)
        .claim("act", "guest")
        .claim("typ", "access")
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(exp))
        .build();

    return sign(claims);
  }

  public int getAccessTtlSeconds() {
    return (int) props.accessTtlSeconds();
  }

  public VerifiedJwt verifyAccessToken(String token) {
    try {
      SignedJWT jwt = parseAndVerify(token);
      JWTClaimsSet c = claims(jwt);

      String typ = c.getStringClaim("typ");
      if (!"access".equals(typ)) {
        throw new JOSEException("Wrong token type");
      }

      String subject = c.getSubject();
      String actor = c.getStringClaim("act");
      if (actor == null || actor.isBlank()) {
        actor = subject != null && subject.startsWith("guest:") ? "guest" : "user";
      }
      String email = c.getStringClaim("email");
      return new VerifiedJwt(subject, actor, email, c.getExpirationTime().toInstant());
    } catch (JOSEException | java.text.ParseException e) {
      // Keep JwtService free of checked exceptions; caller translates to 401.
      throw new IllegalArgumentException("Invalid or expired token", e);
    }
  }

  private static String base64UrlSha256(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    } catch (Exception e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }

  private String sign(JWTClaimsSet claims) {
    try {
      JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
      SignedJWT jwt = new SignedJWT(header, claims);
      jwt.sign(new MACSigner(secret));
      return jwt.serialize();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to sign JWT", e);
    }
  }

  private SignedJWT parseAndVerify(String token) throws JOSEException {
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      if (!jwt.verify(new MACVerifier(secret))) {
        throw new JOSEException("Signature verification failed");
      }
      return jwt;
    } catch (java.text.ParseException e) {
      throw new JOSEException("Token parse failed", e);
    }
  }

  private static JWTClaimsSet claims(SignedJWT jwt) throws JOSEException {
    try {
      return jwt.getJWTClaimsSet();
    } catch (java.text.ParseException e) {
      throw new JOSEException("Claims parse failed", e);
    }
  }

  public record VerifiedJwt(String subject, String actor, String email, Instant expiresAt) {
    public boolean isGuest() {
      return "guest".equalsIgnoreCase(actor);
    }

    public UUID userIdOrNull() {
      if (isGuest()) return null;
      return UUID.fromString(subject);
    }
  }
}
