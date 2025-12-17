package com.example.booking.offers;

import com.example.booking.common.ApiException;
import com.example.booking.common.TraceIds;
import com.example.booking.config.BookingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class OffersClient {
  private final RestClient rest;

  public OffersClient(BookingProperties props) {
    this.rest = RestClient.builder().baseUrl(props.offersBaseUrl()).build();
  }

  public JsonNode priceCheck(String offerId) {
    try {
      var spec = rest.post().uri("/v1/offers/{offerId}/price-check", offerId);
      String traceId = TraceIds.current();
      if (traceId != null && !traceId.isBlank()) {
        spec = spec.header("X-Trace-Id", traceId);
      }
      return spec.retrieve().body(JsonNode.class);
    } catch (HttpClientErrorException.Conflict e) {
      throw new ApiException(HttpStatus.CONFLICT, "OFFER_EXPIRED", "Offer expired or not available");
    } catch (HttpClientErrorException.NotFound e) {
      throw new ApiException(HttpStatus.NOT_FOUND, "OFFER_NOT_FOUND", "Offer not found");
    } catch (HttpClientErrorException e) {
      throw new ApiException(HttpStatus.BAD_GATEWAY, "OFFERS_UNAVAILABLE", "Offers service error");
    }
  }
}
