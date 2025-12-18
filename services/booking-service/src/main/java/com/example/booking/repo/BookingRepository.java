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

  @Query(value = """
      SELECT * FROM bookings
      WHERE owner_type = :ownerType
        AND owner_id = :ownerId
        AND (
          CAST(:status AS varchar) IS NULL
          OR status = CAST(:status AS varchar)
        )
        AND (
          CAST(:fromTs AS timestamptz) IS NULL
          OR created_at >= CAST(:fromTs AS timestamptz)
        )
        AND (
          CAST(:toTs AS timestamptz) IS NULL
          OR created_at <= CAST(:toTs AS timestamptz)
        )
        AND (
          CAST(:cursorCreatedAt AS timestamptz) IS NULL
          OR (created_at < CAST(:cursorCreatedAt AS timestamptz))
          OR (
            created_at = CAST(:cursorCreatedAt AS timestamptz)
            AND id < CAST(:cursorId AS uuid)
          )
        )
      ORDER BY created_at DESC, id DESC
      LIMIT :lim
      """, nativeQuery = true)
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
