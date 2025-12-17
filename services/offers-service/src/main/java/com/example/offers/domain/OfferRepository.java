package com.example.offers.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<OfferEntity, String> {
  Page<OfferEntity> findBySearchIdOrderByDepartAtAscIdAsc(UUID searchId, Pageable pageable);

  long deleteByValidUntilBefore(Instant cutoff);
}
