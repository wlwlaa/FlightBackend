package com.example.booking.api.controllers;

import com.example.booking.api.dto.CreatePaymentIntentRequest;
import com.example.booking.api.dto.CreatePaymentIntentResponse;
import com.example.booking.identity.OwnerRef;
import com.example.booking.identity.OwnerResolver;
import com.example.booking.service.BookingAppService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
public class PaymentsController {

  private final OwnerResolver ownerResolver;
  private final BookingAppService bookingAppService;

  public PaymentsController(OwnerResolver ownerResolver, BookingAppService bookingAppService) {
    this.ownerResolver = ownerResolver;
    this.bookingAppService = bookingAppService;
  }

  @PostMapping("/intent")
  public CreatePaymentIntentResponse intent(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody CreatePaymentIntentRequest req
  ) {
    OwnerRef owner = ownerResolver.resolve(authorization, deviceId);
    return bookingAppService.createPaymentIntent(owner, idempotencyKey, req);
  }
}
