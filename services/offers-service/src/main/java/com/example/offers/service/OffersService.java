package com.example.offers.service;

import com.example.offers.api.dto.*;
import com.example.offers.common.ApiException;
import com.example.offers.domain.OfferEntity;
import com.example.offers.domain.OfferRepository;
import com.example.offers.domain.SearchRequestEntity;
import com.example.offers.domain.SearchRequestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class OffersService {

  private final SearchRequestRepository searchRepo;
  private final OfferRepository offerRepo;
  private final OfferGenerator generator;

  private final int ttlMinutes;
  private final int resultsPerSearch;
  private final int pageSizeDefault;
  private final boolean simulatePriceChange;

  public OffersService(
      SearchRequestRepository searchRepo,
      OfferRepository offerRepo,
      OfferGenerator generator,
      @Value("${offers.ttlMinutes:30}") int ttlMinutes,
      @Value("${offers.resultsPerSearch:60}") int resultsPerSearch,
      @Value("${offers.pageSizeDefault:20}") int pageSizeDefault,
      @Value("${offers.simulatePriceChange:false}") boolean simulatePriceChange
  ) {
    this.searchRepo = searchRepo;
    this.offerRepo = offerRepo;
    this.generator = generator;
    this.ttlMinutes = ttlMinutes;
    this.resultsPerSearch = resultsPerSearch;
    this.pageSizeDefault = pageSizeDefault;
    this.simulatePriceChange = simulatePriceChange;
  }

  @Transactional
  public FlightSearchResponse search(FlightSearchRequest req) {
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    Instant expiresAt = now.plus(ttlMinutes, ChronoUnit.MINUTES);

    UUID searchId = UUID.randomUUID();

    SearchRequestEntity s = new SearchRequestEntity();
    s.setId(searchId);
    s.setFromIata(req.fromIATA().toUpperCase());
    s.setToIata(req.toIATA().toUpperCase());
    s.setDepartDate(req.departDate());
    s.setReturnDate(req.returnDate());
    s.setAdults(req.adults());
    s.setCabin(req.cabin());
    s.setCreatedAt(now);
    s.setExpiresAt(expiresAt);
    searchRepo.save(s);

    List<OfferGenerator.GeneratedOffer> generated = generator.generate(searchId, req, resultsPerSearch, expiresAt);
    for (var g : generated) {
      OfferEntity o = new OfferEntity();
      o.setId(g.id());
      o.setSearchId(searchId);
      o.setFromIata(g.fromIata());
      o.setToIata(g.toIata());
      o.setDepartAt(g.departAt());
      o.setArriveAt(g.arriveAt());
      o.setPriceAmount(g.priceAmount());
      o.setCurrency(g.currency());
      o.setCarrier(g.carrier());
      o.setValidUntil(g.validUntil());
      o.setDetailsJson(g.detailsJson());
      offerRepo.save(o);
    }

    int page = 0;
    int limit = pageSizeDefault;
    var pageRes = offerRepo.findBySearchIdOrderByDepartAtAscIdAsc(searchId, PageRequest.of(page, limit));

    List<FlightOfferSummary> offers = pageRes.getContent().stream().map(this::toSummary).toList();
    String nextCursor = pageRes.hasNext() ? CursorCodec.encode(searchId, page + 1) : null;

    return new FlightSearchResponse(offers, nextCursor);
  }

  @Transactional(readOnly = true)
  public FlightSearchResponse searchByCursor(String cursor, int limit) {
    CursorCodec.Decoded d = CursorCodec.decode(cursor);

    int lim = Math.min(Math.max(limit, 1), 50);
    int page = Math.max(d.page(), 0);

    var pageRes = offerRepo.findBySearchIdOrderByDepartAtAscIdAsc(d.searchId(), PageRequest.of(page, lim));
    List<FlightOfferSummary> offers = pageRes.getContent().stream().map(this::toSummary).toList();
    String nextCursor = pageRes.hasNext() ? CursorCodec.encode(d.searchId(), page + 1) : null;

    return new FlightSearchResponse(offers, nextCursor);
  }

  @Transactional(readOnly = true)
  public OfferDetailsResponse getOfferDetails(String offerId) {
    OfferEntity o = offerRepo.findById(offerId).orElseThrow(() ->
        new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Offer not found")
    );

    if (o.getValidUntil().isBefore(Instant.now())) {
      throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Offer not found");
    }

    JsonNode j = o.getDetailsJson();
    String fareName = safeText(j, "fareName", "Standard");
    boolean refundable = safeBool(j, "refundable", false);
    List<String> baggage = JsonUtil.asStringList(j.get("baggage"));
    List<String> rules = JsonUtil.asStringList(j.get("rules"));
    Money changeFee = null;

    JsonNode cf = j.get("changeFee");
    if (cf != null && cf.isObject()) {
      BigDecimal amt = cf.get("amount") != null ? cf.get("amount").decimalValue() : null;
      String cur = cf.get("currency") != null ? cf.get("currency").asText() : null;
      if (amt != null && cur != null) changeFee = new Money(amt.setScale(2, RoundingMode.HALF_UP), cur);
    }

    return new OfferDetailsResponse(o.getId(), fareName, baggage, rules, refundable, changeFee, o.getValidUntil());
  }

  @Transactional
  public PriceCheckResponse priceCheck(String offerId) {
    OfferEntity o = offerRepo.findById(offerId).orElseThrow(() ->
        new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Offer not found")
    );

    Instant now = Instant.now();
    if (o.getValidUntil().isBefore(now)) {
      throw new ApiException(HttpStatus.CONFLICT, "OFFER_EXPIRED", "Offer expired or not available");
    }

    boolean changed = false;
    if (simulatePriceChange && (now.getEpochSecond() % 13 == 0)) {
      o.setPriceAmount(o.getPriceAmount().add(BigDecimal.valueOf(10.00)).setScale(2, RoundingMode.HALF_UP));
      offerRepo.save(o);
      changed = true;
    }

    return new PriceCheckResponse(toSummary(o), changed);
  }

  private FlightOfferSummary toSummary(OfferEntity o) {
    return new FlightOfferSummary(
        o.getId(),
        o.getFromIata(),
        o.getToIata(),
        o.getDepartAt(),
        o.getArriveAt(),
        new Money(o.getPriceAmount(), o.getCurrency()),
        o.getCarrier(),
        o.getValidUntil()
    );
  }

  private static String safeText(JsonNode node, String field, String def) {
    if (node == null) return def;
    JsonNode v = node.get(field);
    return v != null && !v.isNull() ? v.asText() : def;
  }

  private static boolean safeBool(JsonNode node, String field, boolean def) {
    if (node == null) return def;
    JsonNode v = node.get(field);
    return v != null && !v.isNull() ? v.asBoolean() : def;
  }
}
