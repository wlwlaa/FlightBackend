package com.example.offers.api;

import com.example.offers.api.dto.FlightSearchRequest;
import com.example.offers.api.dto.FlightSearchResponse;
import com.example.offers.service.OffersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/flights")
@Validated
public class FlightsController {

  private final OffersService offersService;

  public FlightsController(OffersService offersService) {
    this.offersService = offersService;
  }

  @PostMapping("/search")
  public FlightSearchResponse search(@Valid @RequestBody FlightSearchRequest req) {
    return offersService.search(req);
  }

  @GetMapping("/search")
  public FlightSearchResponse searchByCursor(
      @RequestParam("cursor") @NotBlank String cursor,
      @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(50) int limit
  ) {
    return offersService.searchByCursor(cursor, limit);
  }
}
