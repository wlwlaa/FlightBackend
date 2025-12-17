package com.example.booking.api.dto;

import java.util.List;
import java.util.UUID;

public record BookingResponse(
    UUID id,
    String createdAt,
    BookingStatus status,
    FlightOfferSnapshot offer,
    ContactDto contact,
    List<PassengerDto> passengers
) {}
