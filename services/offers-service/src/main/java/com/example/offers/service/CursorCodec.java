package com.example.offers.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public final class CursorCodec {

  private CursorCodec() {}

  public static String encode(UUID searchId, int page) {
    String raw = searchId + ":" + page;
    return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static Decoded decode(String cursor) {
    try {
      byte[] bytes = Base64.getUrlDecoder().decode(cursor);
      String raw = new String(bytes, StandardCharsets.UTF_8);
      String[] parts = raw.split(":");
      if (parts.length != 2) throw new IllegalArgumentException("Bad cursor");
      return new Decoded(UUID.fromString(parts[0]), Integer.parseInt(parts[1]));
    } catch (Exception e) {
      throw new IllegalArgumentException("Bad cursor", e);
    }
  }

  public record Decoded(UUID searchId, int page) {}
}
