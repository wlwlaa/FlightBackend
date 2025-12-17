package com.example.catalog.locations;

import com.example.catalog.locations.dto.LocationItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

  private final LocationRepository repo;

  public LocationService(LocationRepository repo) {
    this.repo = repo;
  }

  @Transactional(readOnly = true)
  public List<LocationItem> autocomplete(String query, int limit) {
    String q = query.trim().toLowerCase();
    int lim = Math.min(Math.max(limit, 1), 50);
    return repo.autocomplete(q, lim).stream().map(this::toDto).toList();
  }

  private LocationItem toDto(LocationEntity e) {
    return new LocationItem(e.getCode(), e.getType(), e.getName(), e.getCountry(), e.getCity(), e.getLat(), e.getLon());
  }
}
