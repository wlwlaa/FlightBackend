package com.example.booking.api.dto;

import java.math.BigDecimal;

public record MoneyDto(BigDecimal amount, String currency) {}
