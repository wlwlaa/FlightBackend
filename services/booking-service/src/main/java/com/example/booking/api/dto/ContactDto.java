package com.example.booking.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactDto(
    @Email @NotBlank String email,
    @JsonDeserialize(using = LenientStringDeserializer.class) String phone
) {}
