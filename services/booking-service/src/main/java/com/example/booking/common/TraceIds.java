package com.example.booking.common;

import org.slf4j.MDC;

public final class TraceIds {
  private TraceIds() {}

  /** Returns current trace id from MDC (set by TraceIdFilter), or null if absent. */
  public static String current() {
    return MDC.get("traceId");
  }
}
