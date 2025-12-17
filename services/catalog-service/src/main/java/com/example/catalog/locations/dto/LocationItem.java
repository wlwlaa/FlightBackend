package com.example.catalog.locations.dto;

public record LocationItem(
    String iata,
    String type,
    String name,
    String country,
    String city,
    double lat,
    double lon
) {}
