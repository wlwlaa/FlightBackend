package com.example.booking.api.controllers;

import com.example.booking.api.dto.*;
import com.example.booking.identity.OwnerRef;
import com.example.booking.identity.OwnerResolver;
import com.example.booking.service.BookingAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

import com.example.booking.common.ApiException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/v1/bookings")
@Validated
public class BookingsController {

  private final OwnerResolver ownerResolver;
  private final BookingAppService bookingAppService;

  public BookingsController(OwnerResolver ownerResolver, BookingAppService bookingAppService) {
    this.ownerResolver = ownerResolver;
    this.bookingAppService = bookingAppService;
  }

  @PostMapping
  @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
  public BookingResponse create(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody CreateBookingRequest req
  ) {
    OwnerRef owner = requireOwner(authorization, deviceId);
    return bookingAppService.createBooking(owner, idempotencyKey, req);
  }

  @GetMapping
  public BookingListResponse list(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(50) int limit
  ) {
    OwnerRef owner = requireOwner(authorization, deviceId);
    String st = (status == null || status.isBlank()) ? null : status;
    return bookingAppService.listBookings(owner, st, from, to, cursor, limit);
  }

  @GetMapping("/{bookingId}")
  public BookingResponse get(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @PathVariable UUID bookingId
  ) {
    OwnerRef owner = requireOwner(authorization, deviceId);
    return bookingAppService.getBooking(owner, bookingId);
  }

  @PostMapping("/{bookingId}/cancel")
  public BookingResponse cancel(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @PathVariable UUID bookingId
  ) {
    OwnerRef owner = requireOwner(authorization, deviceId);
    return bookingAppService.cancelBooking(owner, bookingId, idempotencyKey);
  }

  @PostMapping("/{bookingId}/confirm")
  public BookingResponse confirm(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @PathVariable UUID bookingId
  ) {
    OwnerRef owner = requireOwner(authorization, deviceId);
    return bookingAppService.confirmBooking(owner, bookingId, idempotencyKey);
  }

  private OwnerRef requireOwner(String authorization, String deviceId) {
    OwnerRef owner = ownerResolver.resolve(authorization, deviceId);
    if (owner == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
          "Missing Authorization Bearer token or X-Device-Id");
    }
    return owner;
  }
}
