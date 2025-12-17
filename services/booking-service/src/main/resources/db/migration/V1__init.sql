CREATE TABLE bookings (
  id UUID PRIMARY KEY,
  owner_type VARCHAR(8) NOT NULL CHECK (owner_type IN ('user','guest')),
  owner_id VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL CHECK (status IN ('draft','confirmed','canceled')),
  offer_id VARCHAR(128) NOT NULL,
  offer_snapshot JSONB NOT NULL,
  total_amount NUMERIC(12,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  contact JSONB NOT NULL,
  idempotency_key VARCHAR(128),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_owner_created ON bookings (owner_type, owner_id, created_at DESC, id DESC);
CREATE INDEX idx_bookings_status_created ON bookings (status, created_at DESC);

CREATE UNIQUE INDEX uq_bookings_owner_idem
  ON bookings (owner_type, owner_id, idempotency_key)
  WHERE idempotency_key IS NOT NULL;

CREATE TABLE passengers (
  id BIGSERIAL PRIMARY KEY,
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  first_name VARCHAR(128) NOT NULL,
  last_name VARCHAR(128) NOT NULL,
  birth_date TIMESTAMPTZ NOT NULL,
  document_number VARCHAR(64) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_passengers_booking ON passengers (booking_id);

CREATE TABLE payment_intents (
  id UUID PRIMARY KEY,
  booking_id UUID NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
  provider VARCHAR(16) NOT NULL,
  client_secret VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL CHECK (status IN ('created','succeeded','canceled')),
  idempotency_key VARCHAR(128),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_payment_intents_booking_idem
  ON payment_intents (booking_id, idempotency_key)
  WHERE idempotency_key IS NOT NULL;

CREATE TABLE booking_events (
  id BIGSERIAL PRIMARY KEY,
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  type VARCHAR(64) NOT NULL,
  payload JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_booking_events_booking ON booking_events (booking_id, created_at DESC);
