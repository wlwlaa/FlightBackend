package com.example.identity.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

  public static final String TRACE_ID_ATTR = "traceId";
  private static final SecureRandom RNG = new SecureRandom();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String traceId = request.getHeader("X-Trace-Id");
    if (traceId == null || traceId.isBlank()) {
      traceId = randomHex(16);
    }

    request.setAttribute(TRACE_ID_ATTR, traceId);
    MDC.put("traceId", traceId);
    response.setHeader("X-Trace-Id", traceId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove("traceId");
    }
  }

  private static String randomHex(int bytes) {
    byte[] b = new byte[bytes];
    RNG.nextBytes(b);
    StringBuilder sb = new StringBuilder(bytes * 2);
    for (byte x : b) sb.append(String.format("%02x", x));
    return sb.toString();
  }
}
