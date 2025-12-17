CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS locations (
  code CHAR(3) PRIMARY KEY,
  type VARCHAR(16) NOT NULL CHECK (type IN ('city', 'airport')),
  name VARCHAR(255) NOT NULL,
  country VARCHAR(255) NOT NULL,
  city VARCHAR(255),
  lat DOUBLE PRECISION NOT NULL,
  lon DOUBLE PRECISION NOT NULL,
  searchable_text VARCHAR(1024) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_locations_code_lower ON locations (lower(code));
CREATE INDEX IF NOT EXISTS idx_locations_name_lower ON locations (lower(name));
CREATE INDEX IF NOT EXISTS idx_locations_search_trgm ON locations USING GIN (searchable_text gin_trgm_ops);
