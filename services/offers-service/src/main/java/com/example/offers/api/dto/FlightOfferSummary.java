package com.example.offers.api.dto;

import java.time.Instant;

public record FlightOfferSummary(
    String id,
    String fromIATA,
    String toIATA,
    Instant departAt,
    Instant arriveAt,
    Money price,
    String carrier,
    Instant validUntil
) {}
