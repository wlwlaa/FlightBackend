package com.example.offers.service;

import com.example.offers.api.dto.FlightSearchRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OfferGenerator {

  private final ObjectMapper objectMapper;

  public OfferGenerator(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<GeneratedOffer> generate(UUID searchId, FlightSearchRequest req, int count, Instant validUntil) {
    List<GeneratedOffer> out = new ArrayList<>(count);

    String from = req.fromIATA().toUpperCase();
    String to = req.toIATA().toUpperCase();
    String day = DateTimeFormatter.ofPattern("yyyyMMdd")
        .withZone(ZoneOffset.UTC)
        .format(req.departDate());

    String sid = searchId.toString().replace("-", "").substring(0, 8);

    for (int i = 0; i < count; i++) {
      Instant departAt = req.departDate().plus(Duration.ofMinutes(30L * i));
      Instant arriveAt = departAt.plus(Duration.ofMinutes(120 + (i * 3L)));

      BigDecimal amount = BigDecimal.valueOf(120.00 + (i * 5.0)).setScale(2, RoundingMode.HALF_UP);

      String offerId = String.format("%s%s-%s-%s-%02d", from, to, day, sid, i);
      String carrier = (i % 3 == 0) ? "MockAir" : (i % 3 == 1) ? "DemoJet" : "Sample Airlines";

      JsonNode details = buildDetailsJson(i);

      out.add(new GeneratedOffer(
          offerId, from, to, departAt, arriveAt, amount, "EUR", carrier, validUntil, details
      ));
    }

    return out;
  }

  private JsonNode buildDetailsJson(int i) {
    String fareName = (i % 2 == 0) ? "Standard" : "Basic";
    boolean refundable = false;

    ObjectNode root = objectMapper.createObjectNode();
    root.put("fareName", fareName);
    root.put("refundable", refundable);

    var baggage = root.putArray("baggage");
    baggage.add("Personal item");
    baggage.add((i % 2 == 0) ? "Cabin bag (8kg)" : "No cabin bag");

    var rules = root.putArray("rules");
    rules.add("Changes allowed with fee");
    rules.add("Non-refundable");

    ObjectNode changeFee = objectMapper.createObjectNode();
    changeFee.put("amount", 35.0);
    changeFee.put("currency", "EUR");
    root.set("changeFee", changeFee);

    return root;
  }

  public record GeneratedOffer(
      String id,
      String fromIata,
      String toIata,
      Instant departAt,
      Instant arriveAt,
      BigDecimal priceAmount,
      String currency,
      String carrier,
      Instant validUntil,
      JsonNode detailsJson
  ) {}
}
