package com.example.booking.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

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

  @Column(columnDefinition = "jsonb")
  private String payload;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() { if (createdAt == null) createdAt = Instant.now(); }

  public BookingEventEntity() {}
  public BookingEventEntity(UUID bookingId, String type, String payload) {
    this.bookingId = bookingId;
    this.type = type;
    this.payload = payload;
  }
}
