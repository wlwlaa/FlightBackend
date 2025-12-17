package com.example.booking.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public final class CursorCodec {
  private CursorCodec() {}

  public record Cursor(Instant createdAt, UUID id) {}

  public static String encode(Instant createdAt, UUID id) {
    String raw = createdAt.toEpochMilli() + ":" + id;
    return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static Cursor decode(String cursor) {
    if (cursor == null || cursor.isBlank()) return null;
    String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
    int idx = raw.indexOf(':');
    if (idx <= 0) throw new IllegalArgumentException("Invalid cursor");
    long ms = Long.parseLong(raw.substring(0, idx));
    UUID id = UUID.fromString(raw.substring(idx + 1));
    return new Cursor(Instant.ofEpochMilli(ms), id);
  }
}
