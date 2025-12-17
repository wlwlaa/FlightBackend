package com.example.booking.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassengerDto(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull String birthDate,
    @NotBlank String documentNumber
) {}
