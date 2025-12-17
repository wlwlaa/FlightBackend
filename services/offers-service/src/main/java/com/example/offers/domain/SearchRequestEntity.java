package com.example.offers.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "search_requests")
public class SearchRequestEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "from_iata", nullable = false, length = 3)
  private String fromIata;

  @Column(name = "to_iata", nullable = false, length = 3)
  private String toIata;

  @Column(name = "depart_date", nullable = false)
  private Instant departDate;

  @Column(name = "return_date")
  private Instant returnDate;

  @Column(name = "adults", nullable = false)
  private int adults;

  @Column(name = "cabin", nullable = false, length = 32)
  private String cabin;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  public SearchRequestEntity() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public String getFromIata() { return fromIata; }
  public void setFromIata(String fromIata) { this.fromIata = fromIata; }

  public String getToIata() { return toIata; }
  public void setToIata(String toIata) { this.toIata = toIata; }

  public Instant getDepartDate() { return departDate; }
  public void setDepartDate(Instant departDate) { this.departDate = departDate; }

  public Instant getReturnDate() { return returnDate; }
  public void setReturnDate(Instant returnDate) { this.returnDate = returnDate; }

  public int getAdults() { return adults; }
  public void setAdults(int adults) { this.adults = adults; }

  public String getCabin() { return cabin; }
  public void setCabin(String cabin) { this.cabin = cabin; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
