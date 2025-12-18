package com.example.booking.api.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Accepts scalar JSON values (string/number/boolean) and coerces them to String.
 * Helps clients that send phone numbers as numbers.
 */
public class LenientStringDeserializer extends JsonDeserializer<String> {
  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonToken token = p.currentToken();
    if (token == JsonToken.VALUE_NULL) return null;
    if (token != null && token.isScalarValue()) return p.getValueAsString();
    ctxt.reportInputMismatch(String.class, "Expected a scalar JSON value");
    return null;
  }
}
