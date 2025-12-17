package com.example.offers.api.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record FlightSearchRequest(
    @NotBlank @Size(min = 3, max = 3) String fromIATA,
    @NotBlank @Size(min = 3, max = 3) String toIATA,
    @NotNull Instant departDate,
    Instant returnDate,
    @NotNull @Min(1) @Max(9) Integer adults,
    @NotBlank String cabin
) {}
