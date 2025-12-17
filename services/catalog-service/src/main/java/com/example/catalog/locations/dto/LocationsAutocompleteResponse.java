package com.example.catalog.locations.dto;

import java.util.List;

public record LocationsAutocompleteResponse(
    List<LocationItem> items
) {}
