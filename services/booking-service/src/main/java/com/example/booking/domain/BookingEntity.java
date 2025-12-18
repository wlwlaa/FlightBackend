package com.example.booking.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "bookings")
public class BookingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @Column(name = "owner_type", nullable = false, length = 8)
  private String ownerType;

  @Column(name = "owner_id", nullable = false, length = 128)
  private String ownerId;

  @Column(nullable = false, length = 16)
  private String status;

  @Column(name = "offer_id", nullable = false, length = 128)
  private String offerId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "offer_snapshot", nullable = false, columnDefinition = "jsonb")
  private JsonNode offerSnapshot;

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Column(nullable = false, length = 3)
  private String currency;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private JsonNode contact;

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
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }
  public JsonNode getOfferSnapshot() { return offerSnapshot; }
  public void setOfferSnapshot(JsonNode offerSnapshot) { this.offerSnapshot = offerSnapshot; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public JsonNode getContact() { return contact; }
  public void setContact(JsonNode contact) { this.contact = contact; }
  public String getIdempotencyKey() { return idempotencyKey; }
  public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
