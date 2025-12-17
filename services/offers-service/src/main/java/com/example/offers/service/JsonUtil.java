package com.example.offers.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonUtil {
  private JsonUtil() {}

  public static List<String> asStringList(JsonNode node) {
    if (node == null || node.isNull()) return Collections.emptyList();
    if (!node.isArray()) return Collections.emptyList();
    List<String> out = new ArrayList<>();
    for (JsonNode n : node) out.add(n.asText());
    return out;
  }
}
