package com.example.booking.repo;

import com.example.booking.domain.PaymentIntentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntentEntity, UUID> {
  Optional<PaymentIntentEntity> findByBookingId(UUID bookingId);
  Optional<PaymentIntentEntity> findByBookingIdAndIdempotencyKey(UUID bookingId, String idempotencyKey);
}
