package com.example.booking.repo;

import com.example.booking.domain.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
  List<PassengerEntity> findByBooking_Id(UUID bookingId);
}
