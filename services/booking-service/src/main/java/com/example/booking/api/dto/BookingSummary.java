package com.example.booking.api.dto;

import java.util.UUID;

public record BookingSummary(UUID id, String createdAt, BookingStatus status, FlightOfferSnapshot offer) {}
