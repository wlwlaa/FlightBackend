package com.example.booking.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "booking_events")
public class BookingEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "booking_id", nullable = false)
  private UUID bookingId;

  @Column(nullable = false, length = 64)
  private String type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "payload", columnDefinition = "jsonb")
  private JsonNode payload;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() { if (createdAt == null) createdAt = Instant.now(); }

  public BookingEventEntity() {}

  public BookingEventEntity(UUID bookingId, String type, JsonNode payload) {
    this.bookingId = bookingId;
    this.type = type;
    this.payload = payload;
  }

  public Long getId() { return id; }
  public UUID getBookingId() { return bookingId; }
  public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public JsonNode getPayload() { return payload; }
  public void setPayload(JsonNode payload) { this.payload = payload; }
  public Instant getCreatedAt() { return createdAt; }
}
