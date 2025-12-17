package com.example.offers.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "offers")
public class OfferEntity {

  @Id
  @Column(name = "id", nullable = false, length = 64)
  private String id;

  @Column(name = "search_id", nullable = false)
  private UUID searchId;

  @Column(name = "from_iata", nullable = false, length = 3)
  private String fromIata;

  @Column(name = "to_iata", nullable = false, length = 3)
  private String toIata;

  @Column(name = "depart_at", nullable = false)
  private Instant departAt;

  @Column(name = "arrive_at", nullable = false)
  private Instant arriveAt;

  @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal priceAmount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "carrier", nullable = false, length = 64)
  private String carrier;

  @Column(name = "valid_until", nullable = false)
  private Instant validUntil;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "details_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode detailsJson;

  public OfferEntity() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public UUID getSearchId() { return searchId; }
  public void setSearchId(UUID searchId) { this.searchId = searchId; }

  public String getFromIata() { return fromIata; }
  public void setFromIata(String fromIata) { this.fromIata = fromIata; }

  public String getToIata() { return toIata; }
  public void setToIata(String toIata) { this.toIata = toIata; }

  public Instant getDepartAt() { return departAt; }
  public void setDepartAt(Instant departAt) { this.departAt = departAt; }

  public Instant getArriveAt() { return arriveAt; }
  public void setArriveAt(Instant arriveAt) { this.arriveAt = arriveAt; }

  public BigDecimal getPriceAmount() { return priceAmount; }
  public void setPriceAmount(BigDecimal priceAmount) { this.priceAmount = priceAmount; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public String getCarrier() { return carrier; }
  public void setCarrier(String carrier) { this.carrier = carrier; }

  public Instant getValidUntil() { return validUntil; }
  public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }

  public JsonNode getDetailsJson() { return detailsJson; }
  public void setDetailsJson(JsonNode detailsJson) { this.detailsJson = detailsJson; }
}
