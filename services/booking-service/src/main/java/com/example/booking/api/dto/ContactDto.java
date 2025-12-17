package com.example.booking.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactDto(@Email @NotBlank String email, String phone) {}
