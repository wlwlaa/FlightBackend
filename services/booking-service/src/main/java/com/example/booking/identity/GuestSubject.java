package com.example.booking.identity;

import com.example.booking.config.BookingProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class GuestSubject {
  private GuestSubject() {}

  public static String fromDeviceId(String deviceId, BookingProperties props) {
    String salt = props.guestSubjectSalt() == null ? "" : props.guestSubjectSalt();
    return "guest:" + base64UrlSha256(salt + deviceId);
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
}
