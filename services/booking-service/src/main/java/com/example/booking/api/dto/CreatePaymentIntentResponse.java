package com.example.booking.api.dto;

public record CreatePaymentIntentResponse(String provider, String clientSecret) {}
