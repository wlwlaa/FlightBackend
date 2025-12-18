package com.example.booking.service;

import com.example.booking.api.dto.*;
import com.example.booking.common.ApiException;
import com.example.booking.domain.BookingEntity;
import com.example.booking.domain.BookingEventEntity;
import com.example.booking.domain.PassengerEntity;
import com.example.booking.domain.PaymentIntentEntity;
import com.example.booking.identity.OwnerRef;
import com.example.booking.offers.OffersClient;
import com.example.booking.repo.BookingEventRepository;
import com.example.booking.repo.BookingRepository;
import com.example.booking.repo.PassengerRepository;
import com.example.booking.repo.PaymentIntentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class BookingAppService {

  private final BookingRepository bookingRepo;
  private final PassengerRepository passengerRepo;
  private final PaymentIntentRepository paymentRepo;
  private final BookingEventRepository eventRepo;
  private final OffersClient offersClient;
  private final ObjectMapper om;

  public BookingAppService(
      BookingRepository bookingRepo,
      PassengerRepository passengerRepo,
      PaymentIntentRepository paymentRepo,
      BookingEventRepository eventRepo,
      OffersClient offersClient,
      ObjectMapper om
  ) {
    this.bookingRepo = bookingRepo;
    this.passengerRepo = passengerRepo;
    this.paymentRepo = paymentRepo;
    this.eventRepo = eventRepo;
    this.offersClient = offersClient;
    this.om = om;
  }

  @Transactional
  public BookingResponse createBooking(OwnerRef owner, String idempotencyKey, CreateBookingRequest req) {
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var existing = bookingRepo.findByOwnerTypeAndOwnerIdAndIdempotencyKey(owner.type().name(), owner.id(), idempotencyKey);
      if (existing.isPresent()) {
        return toBookingResponse(existing.get(), passengerRepo.findByBooking_Id(existing.get().getId()));
      }
    }

    JsonNode priceCheck = offersClient.priceCheck(req.offerId());
    if (priceCheck == null || priceCheck.get("offer") == null) {
      throw new ApiException(HttpStatus.BAD_GATEWAY, "OFFERS_UNAVAILABLE", "Offers service did not return offer");
    }

    JsonNode offer = priceCheck.get("offer");
    BigDecimal amount = offer.path("price").path("amount").decimalValue();
    String currency = offer.path("price").path("currency").asText();

    BookingEntity b = new BookingEntity();
    b.setOwnerType(owner.type().name());
    b.setOwnerId(owner.id());
    b.setStatus(BookingStatus.draft.name());
    b.setOfferId(req.offerId());
    b.setOfferSnapshot(offer);
    b.setTotalAmount(amount);
    b.setCurrency(currency);
    b.setContact(serializeContact(req.contact()));
    b.setIdempotencyKey((idempotencyKey == null || idempotencyKey.isBlank()) ? null : idempotencyKey);
    Instant now = Instant.now();
    b.setCreatedAt(now);
    b.setUpdatedAt(now);

    bookingRepo.save(b);
    eventRepo.save(new BookingEventEntity(b.getId(), "booking_created", null));

    List<PassengerEntity> passengers = new ArrayList<>();
    for (PassengerDto p : req.passengers()) {
      PassengerEntity pe = new PassengerEntity();
      pe.setBooking(b);
      pe.setFirstName(p.firstName());
      pe.setLastName(p.lastName());
      pe.setBirthDate(parseInstant(p.birthDate(), "passengers.birthDate"));
      pe.setDocumentNumber(p.documentNumber());
      passengers.add(pe);
    }
    passengerRepo.saveAll(passengers);

    return toBookingResponse(b, passengers);
  }

  @Transactional(readOnly = true)
  public BookingListResponse listBookings(OwnerRef owner, String status, Instant from, Instant to, String cursor, int limit) {
    int lim = Math.min(Math.max(limit, 1), 50);
    CursorCodec.Cursor c = CursorCodec.decode(cursor);
    Instant cursorCreatedAt = c == null ? null : c.createdAt();
    UUID cursorId = c == null ? null : c.id();

    List<BookingEntity> page = bookingRepo.listPage(
        owner.type().name(), owner.id(),
        status,
        from, to,
        cursorCreatedAt, cursorId,
        lim + 1
    );

    boolean hasMore = page.size() > lim;
    if (hasMore) page = page.subList(0, lim);

    List<BookingSummary> items = page.stream().map(this::toBookingSummary).toList();

    String nextCursor = null;
    if (hasMore && !page.isEmpty()) {
      BookingEntity last = page.get(page.size() - 1);
      nextCursor = CursorCodec.encode(last.getCreatedAt(), last.getId());
    }
    return new BookingListResponse(items, nextCursor);
  }

  @Transactional(readOnly = true)
  public BookingResponse getBooking(OwnerRef owner, UUID bookingId) {
    BookingEntity b = bookingRepo.findByOwnerTypeAndOwnerIdAndId(owner.type().name(), owner.id(), bookingId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Booking not found"));
    return toBookingResponse(b, passengerRepo.findByBooking_Id(b.getId()));
  }

  @Transactional
  public BookingResponse cancelBooking(OwnerRef owner, UUID bookingId, String idempotencyKey) {
    BookingEntity b = bookingRepo.findByOwnerTypeAndOwnerIdAndId(owner.type().name(), owner.id(), bookingId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Booking not found"));

    if (BookingStatus.canceled.name().equals(b.getStatus())) {
      return toBookingResponse(b, passengerRepo.findByBooking_Id(b.getId()));
    }

    if (!BookingStatus.draft.name().equals(b.getStatus()) && !BookingStatus.confirmed.name().equals(b.getStatus())) {
      throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATE", "Booking cannot be canceled from state: " + b.getStatus());
    }

    b.setStatus(BookingStatus.canceled.name());
    bookingRepo.save(b);
    eventRepo.save(new BookingEventEntity(b.getId(), "booking_canceled", null));
    return toBookingResponse(b, passengerRepo.findByBooking_Id(b.getId()));
  }

  @Transactional
  public CreatePaymentIntentResponse createPaymentIntent(OwnerRef owner, String idempotencyKey, CreatePaymentIntentRequest req) {
    BookingEntity b = bookingRepo.findByOwnerTypeAndOwnerIdAndId(owner.type().name(), owner.id(), req.bookingId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Booking not found"));

    if (!BookingStatus.draft.name().equals(b.getStatus())) {
      throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATE", "Booking must be draft to create payment intent");
    }

    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var existing = paymentRepo.findByBookingIdAndIdempotencyKey(b.getId(), idempotencyKey);
      if (existing.isPresent()) {
        return new CreatePaymentIntentResponse(existing.get().getProvider(), existing.get().getClientSecret());
      }
    }

    if (req.amount() != null && b.getTotalAmount().compareTo(req.amount()) != 0) {
      throw new ApiException(HttpStatus.CONFLICT, "AMOUNT_MISMATCH", "Amount mismatch");
    }
    if (req.currency() != null && !b.getCurrency().equalsIgnoreCase(req.currency())) {
      throw new ApiException(HttpStatus.CONFLICT, "CURRENCY_MISMATCH", "Currency mismatch");
    }

    PaymentIntentEntity existing = paymentRepo.findByBookingId(b.getId()).orElse(null);
    if (existing != null && !"canceled".equals(existing.getStatus())) {
      return new CreatePaymentIntentResponse(existing.getProvider(), existing.getClientSecret());
    }

    PaymentIntentEntity created = new PaymentIntentEntity();
    created.setBookingId(b.getId());
    created.setProvider("mock");
    created.setClientSecret("mock_secret_" + UUID.randomUUID());
    created.setStatus("created");
    created.setIdempotencyKey((idempotencyKey == null || idempotencyKey.isBlank()) ? null : idempotencyKey);
    Instant now = Instant.now();
    created.setCreatedAt(now);
    created.setUpdatedAt(now);

    paymentRepo.save(created);
    eventRepo.save(new BookingEventEntity(b.getId(), "payment_intent_created", null));
    return new CreatePaymentIntentResponse(created.getProvider(), created.getClientSecret());
  }

  @Transactional
  public BookingResponse confirmBooking(OwnerRef owner, UUID bookingId, String idempotencyKey) {
    BookingEntity b = bookingRepo.findByOwnerTypeAndOwnerIdAndId(owner.type().name(), owner.id(), bookingId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Booking not found"));

    if (BookingStatus.confirmed.name().equals(b.getStatus())) {
      return toBookingResponse(b, passengerRepo.findByBooking_Id(b.getId()));
    }
    if (!BookingStatus.draft.name().equals(b.getStatus())) {
      throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATE", "Booking must be draft to confirm");
    }

    PaymentIntentEntity pi = paymentRepo.findByBookingId(b.getId())
        .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "PAYMENT_REQUIRED", "Payment intent is required"));

    pi.setStatus("succeeded");
    paymentRepo.save(pi);

    b.setStatus(BookingStatus.confirmed.name());
    bookingRepo.save(b);
    eventRepo.save(new BookingEventEntity(b.getId(), "booking_confirmed", null));
    return toBookingResponse(b, passengerRepo.findByBooking_Id(b.getId()));
  }

  private BookingSummary toBookingSummary(BookingEntity b) {
    FlightOfferSnapshot offer = parseOfferSnapshot(b.getOfferSnapshot());
    return new BookingSummary(b.getId(), b.getCreatedAt().toString(), BookingStatus.valueOf(b.getStatus()), offer);
  }

  private BookingResponse toBookingResponse(BookingEntity b, List<PassengerEntity> passengers) {
    FlightOfferSnapshot offer = parseOfferSnapshot(b.getOfferSnapshot());
    ContactDto contact = parseContact(b.getContact());
    List<PassengerDto> pax = passengers.stream()
        .map(p -> new PassengerDto(p.getFirstName(), p.getLastName(), p.getBirthDate().toString(), p.getDocumentNumber()))
        .toList();
    return new BookingResponse(b.getId(), b.getCreatedAt().toString(), BookingStatus.valueOf(b.getStatus()), offer, contact, pax);
  }

  private FlightOfferSnapshot parseOfferSnapshot(JsonNode n) {
    try {
      if (n == null || n.isNull()) {
        throw new IllegalArgumentException("snapshot is null");
      }
      JsonNode price = n.path("price");
      MoneyDto money = new MoneyDto(price.path("amount").decimalValue(), price.path("currency").asText());
      return new FlightOfferSnapshot(
          n.path("id").asText(),
          n.path("fromIATA").asText(),
          n.path("toIATA").asText(),
          n.path("departAt").asText(),
          n.path("arriveAt").asText(),
          money,
          n.path("carrier").asText()
      );
    } catch (Exception e) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "BAD_SNAPSHOT", "Failed to parse offer snapshot");
    }
  }

  private JsonNode serializeContact(ContactDto c) {
    ObjectNode node = om.createObjectNode();
    node.put("email", c.email());
    if (c.phone() != null) node.put("phone", c.phone());
    return node;
  }

  private ContactDto parseContact(JsonNode n) {
    try {
      if (n == null || n.isNull()) return new ContactDto("", null);
      String email = n.path("email").asText();
      String phone = n.hasNonNull("phone") ? n.get("phone").asText() : null;
      return new ContactDto(email, phone);
    } catch (Exception e) {
      return new ContactDto("", null);
    }
  }

  private Instant parseInstant(String iso, String fieldName) {
    try {
      return OffsetDateTime.parse(iso).toInstant();
    } catch (Exception e) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid ISO date-time for " + fieldName, Map.of("value", iso));
    }
  }
}
