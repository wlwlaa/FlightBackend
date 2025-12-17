package com.example.booking.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_intents")
public class PaymentIntentEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "booking_id", nullable = false)
  private UUID bookingId;

  @Column(nullable = false, length = 16)
  private String provider;

  @Column(name = "client_secret", nullable = false, length = 128)
  private String clientSecret;

  @Column(nullable = false, length = 16)
  private String status;

  @Column(name = "idempotency_key", length = 128)
  private String idempotencyKey;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    if (createdAt == null) createdAt = now;
    if (updatedAt == null) updatedAt = now;
  }

  @PreUpdate
  void preUpdate() { updatedAt = Instant.now(); }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getBookingId() { return bookingId; }
  public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
  public String getProvider() { return provider; }
  public void setProvider(String provider) { this.provider = provider; }
  public String getClientSecret() { return clientSecret; }
  public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getIdempotencyKey() { return idempotencyKey; }
  public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
