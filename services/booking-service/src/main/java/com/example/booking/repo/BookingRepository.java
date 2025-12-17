package com.example.booking.repo;

import com.example.booking.domain.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
  Optional<BookingEntity> findByOwnerTypeAndOwnerIdAndId(String ownerType, String ownerId, UUID id);
  Optional<BookingEntity> findByOwnerTypeAndOwnerIdAndIdempotencyKey(String ownerType, String ownerId, String idempotencyKey);

  @Query(value = "SELECT * FROM bookings WHERE owner_type = :ownerType AND owner_id = :ownerId AND (:status IS NULL OR status = :status) AND (:fromTs IS NULL OR created_at >= :fromTs) AND (:toTs IS NULL OR created_at <= :toTs) AND (:cursorCreatedAt IS NULL OR (created_at < :cursorCreatedAt) OR (created_at = :cursorCreatedAt AND id < :cursorId)) ORDER BY created_at DESC, id DESC LIMIT :lim", nativeQuery = true)
  List<BookingEntity> listPage(
      @Param("ownerType") String ownerType,
      @Param("ownerId") String ownerId,
      @Param("status") String status,
      @Param("fromTs") Instant fromTs,
      @Param("toTs") Instant toTs,
      @Param("cursorCreatedAt") Instant cursorCreatedAt,
      @Param("cursorId") UUID cursorId,
      @Param("lim") int lim
  );
}
