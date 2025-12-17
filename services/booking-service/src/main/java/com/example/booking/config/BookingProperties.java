package com.example.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booking")
public record BookingProperties(String identityBaseUrl, String offersBaseUrl, String guestSubjectSalt) {}
