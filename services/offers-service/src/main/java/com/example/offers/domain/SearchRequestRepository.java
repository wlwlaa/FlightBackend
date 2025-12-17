package com.example.offers.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchRequestRepository extends JpaRepository<SearchRequestEntity, UUID> {}
