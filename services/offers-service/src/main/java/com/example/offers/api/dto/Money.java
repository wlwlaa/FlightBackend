package com.example.offers.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record Money(
    @NotNull BigDecimal amount,
    @NotBlank String currency
) {}
