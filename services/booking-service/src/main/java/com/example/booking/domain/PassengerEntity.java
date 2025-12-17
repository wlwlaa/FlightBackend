package com.example.booking.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "passengers")
public class PassengerEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private BookingEntity booking;

  @Column(name = "first_name", nullable = false, length = 128)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 128)
  private String lastName;

  @Column(name = "birth_date", nullable = false)
  private Instant birthDate;

  @Column(name = "document_number", nullable = false, length = 64)
  private String documentNumber;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() { if (createdAt == null) createdAt = Instant.now(); }

  public Long getId() { return id; }
  public BookingEntity getBooking() { return booking; }
  public void setBooking(BookingEntity booking) { this.booking = booking; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public Instant getBirthDate() { return birthDate; }
  public void setBirthDate(Instant birthDate) { this.birthDate = birthDate; }
  public String getDocumentNumber() { return documentNumber; }
  public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
}
