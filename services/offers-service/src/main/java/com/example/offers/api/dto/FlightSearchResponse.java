package com.example.offers.api.dto;

import java.util.List;

public record FlightSearchResponse(
    List<FlightOfferSummary> offers,
    String nextCursor
) {}
