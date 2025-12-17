package com.example.identity.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest req) {
    return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(
        ex.getCode(),
        ex.getMessage(),
        ex.getDetails(),
        traceId(req)
    ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, Object> details = new HashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      details.put(fe.getField(), fe.getDefaultMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
        "VALIDATION_ERROR",
        "Validation failed",
        details,
        traceId(req)
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
        "INTERNAL_ERROR",
        "Unexpected error",
        null,
        traceId(req)
    ));
  }

  private static String traceId(HttpServletRequest req) {
    Object v = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
    return v == null ? "unknown" : v.toString();
  }
}
