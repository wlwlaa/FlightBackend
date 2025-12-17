package com.example.catalog.locations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "locations")
public class LocationEntity {

  @Id
  @Column(name = "code", nullable = false, length = 3)
  private String code;

  @Column(name = "type", nullable = false, length = 16)
  private String type; // city | airport

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "country", nullable = false, length = 255)
  private String country;

  @Column(name = "city", length = 255)
  private String city;

  @Column(name = "lat", nullable = false)
  private double lat;

  @Column(name = "lon", nullable = false)
  private double lon;

  @Column(name = "searchable_text", nullable = false, length = 1024)
  private String searchableText;

  public LocationEntity() {}

  public String getCode() { return code; }
  public String getType() { return type; }
  public String getName() { return name; }
  public String getCountry() { return country; }
  public String getCity() { return city; }
  public double getLat() { return lat; }
  public double getLon() { return lon; }
  public String getSearchableText() { return searchableText; }

  public void setCode(String code) { this.code = code; }
  public void setType(String type) { this.type = type; }
  public void setName(String name) { this.name = name; }
  public void setCountry(String country) { this.country = country; }
  public void setCity(String city) { this.city = city; }
  public void setLat(double lat) { this.lat = lat; }
  public void setLon(double lon) { this.lon = lon; }
  public void setSearchableText(String searchableText) { this.searchableText = searchableText; }
}
