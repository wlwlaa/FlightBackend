CREATE TABLE search_requests (
  id UUID PRIMARY KEY,
  from_iata VARCHAR(3) NOT NULL,
  to_iata VARCHAR(3) NOT NULL,
  depart_date TIMESTAMPTZ NOT NULL,
  return_date TIMESTAMPTZ NULL,
  adults INT NOT NULL,
  cabin VARCHAR(32) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE offers (
  id VARCHAR(64) PRIMARY KEY,
  search_id UUID NOT NULL REFERENCES search_requests(id) ON DELETE CASCADE,
  from_iata VARCHAR(3) NOT NULL,
  to_iata VARCHAR(3) NOT NULL,
  depart_at TIMESTAMPTZ NOT NULL,
  arrive_at TIMESTAMPTZ NOT NULL,
  price_amount NUMERIC(12,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  carrier VARCHAR(64) NOT NULL,
  valid_until TIMESTAMPTZ NOT NULL,
  details_json JSONB NOT NULL
);

CREATE INDEX idx_offers_search_depart ON offers (search_id, depart_at, id);
CREATE INDEX idx_offers_valid_until ON offers (valid_until);
CREATE INDEX idx_search_expires ON search_requests (expires_at);
