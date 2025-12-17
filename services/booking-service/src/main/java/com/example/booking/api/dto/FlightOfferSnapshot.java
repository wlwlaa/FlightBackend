package com.example.booking.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlightOfferSnapshot(
    String id,
    String fromIATA,
    String toIATA,
    String departAt,
    String arriveAt,
    MoneyDto price,
    String carrier
) {}
