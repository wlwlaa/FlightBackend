package com.example.offers.api.dto;

import java.time.Instant;
import java.util.List;

public record OfferDetailsResponse(
    String offerId,
    String fareName,
    List<String> baggage,
    List<String> rules,
    boolean refundable,
    Money changeFee,
    Instant validUntil
) {}
