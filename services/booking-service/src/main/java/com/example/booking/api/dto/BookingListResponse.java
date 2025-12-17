package com.example.booking.api.dto;

import java.util.List;

public record BookingListResponse(List<BookingSummary> items, String nextCursor) {}
