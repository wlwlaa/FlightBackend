package com.example.booking.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentIntentRequest(@NotNull UUID bookingId, BigDecimal amount, String currency) {}
