package com.example.booking.identity;

import com.example.booking.common.ApiException;
import com.example.booking.common.TraceIds;
import com.example.booking.config.BookingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class IdentityClient {
  private final RestClient rest;

  public IdentityClient(BookingProperties props) {
    this.rest = RestClient.builder().baseUrl(props.identityBaseUrl()).build();
  }

  public OwnerRef resolveOwnerFromBearer(String authorizationHeader) {
    try {
      var spec = rest.get().uri("/v1/me").header("Authorization", authorizationHeader);
      String traceId = TraceIds.current();
      if (traceId != null && !traceId.isBlank()) {
        spec = spec.header("X-Trace-Id", traceId);
      }

      JsonNode node = spec.retrieve().body(JsonNode.class);
      if (node == null || node.get("mode") == null || node.get("id") == null) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid token");
      }
      String mode = node.get("mode").asText();
      String id = node.get("id").asText();
      if ("user".equals(mode)) return new OwnerRef(OwnerType.user, id);
      if ("guest".equals(mode)) return new OwnerRef(OwnerType.guest, id);
      throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid token mode");
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid or expired token");
    } catch (HttpClientErrorException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token validation failed");
    }
  }
}
