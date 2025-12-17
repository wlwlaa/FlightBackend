package com.example.booking.repo;

import com.example.booking.domain.BookingEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingEventRepository extends JpaRepository<BookingEventEntity, Long> {}
