package com.example.offers.api.dto;

public record PriceCheckResponse(
    FlightOfferSummary offer,
    boolean priceChanged
) {}
