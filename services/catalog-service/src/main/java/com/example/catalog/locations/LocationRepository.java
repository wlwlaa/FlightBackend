package com.example.catalog.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {

  @Query(value =
      "SELECT * " +
      "FROM locations " +
      "WHERE lower(code) = :q OR searchable_text LIKE ('%' || :q || '%') " +
      "ORDER BY " +
      "  CASE " +
      "    WHEN lower(code) = :q THEN 0 " +
      "    WHEN lower(code) LIKE (:q || '%') THEN 1 " +
      "    WHEN lower(name) LIKE (:q || '%') THEN 2 " +
      "    WHEN searchable_text LIKE (:q || '%') THEN 3 " +
      "    ELSE 4 " +
      "  END, " +
      "  name ASC " +
      "LIMIT :lim",
      nativeQuery = true)
  List<LocationEntity> autocomplete(@Param("q") String q, @Param("lim") int lim);
}
