package com.example.booking.identity;

import com.example.booking.common.ApiException;
import com.example.booking.config.BookingProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OwnerResolver {
  private final IdentityClient identityClient;
  private final BookingProperties props;

  public OwnerResolver(IdentityClient identityClient, BookingProperties props) {
    this.identityClient = identityClient;
    this.props = props;
  }

  public OwnerRef resolve(String authorizationHeader, String deviceIdHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return identityClient.resolveOwnerFromBearer(authorizationHeader);
    }
    if (deviceIdHeader != null && !deviceIdHeader.isBlank()) {
      return new OwnerRef(OwnerType.guest, GuestSubject.fromDeviceId(deviceIdHeader, props));
    }
    throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Provide Authorization: Bearer <token> or X-Device-Id header");
  }
}
