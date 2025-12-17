package com.example.offers.service;

import com.example.offers.domain.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class OffersCleanupJob {

  private static final Logger log = LoggerFactory.getLogger(OffersCleanupJob.class);

  private final OfferRepository offerRepo;
  private final long cleanupMs;

  public OffersCleanupJob(OfferRepository offerRepo, @Value("${offers.cleanupMs:600000}") long cleanupMs) {
    this.offerRepo = offerRepo;
    this.cleanupMs = cleanupMs;
  }

  @Scheduled(fixedDelayString = "${offers.cleanupMs:600000}")
  @Transactional
  public void cleanup() {
    long deleted = offerRepo.deleteByValidUntilBefore(Instant.now());
    if (deleted > 0) log.info("Cleaned up {} expired offers", deleted);
  }
}
