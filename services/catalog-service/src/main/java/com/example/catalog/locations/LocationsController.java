package com.example.catalog.locations;

import com.example.catalog.locations.dto.LocationsAutocompleteResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/locations")
@Validated
public class LocationsController {

  private final LocationService locationService;

  public LocationsController(LocationService locationService) {
    this.locationService = locationService;
  }

  @GetMapping("/autocomplete")
  public LocationsAutocompleteResponse autocomplete(
      @RequestParam("query") @NotBlank String query,
      @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(50) int limit
  ) {
    return new LocationsAutocompleteResponse(locationService.autocomplete(query, limit));
  }
}
