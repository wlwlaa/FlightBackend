package com.example.booking.common;

import java.util.Map;

public record ErrorResponse(String code, String message, Map<String,Object> details, String traceId) {}
