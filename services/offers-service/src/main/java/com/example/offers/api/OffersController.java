package com.example.offers.api;

import com.example.offers.api.dto.OfferDetailsResponse;
import com.example.offers.api.dto.PriceCheckResponse;
import com.example.offers.service.OffersService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/offers")
@Validated
public class OffersController {

  private final OffersService offersService;

  public OffersController(OffersService offersService) {
    this.offersService = offersService;
  }

  @GetMapping("/{offerId}")
  public OfferDetailsResponse getOffer(@PathVariable("offerId") @NotBlank String offerId) {
    return offersService.getOfferDetails(offerId);
  }

  @PostMapping("/{offerId}/price-check")
  public PriceCheckResponse priceCheck(@PathVariable("offerId") @NotBlank String offerId) {
    return offersService.priceCheck(offerId);
  }
}
