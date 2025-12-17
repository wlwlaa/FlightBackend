-- Convert bpchar(3) -> varchar(3) and remove padding spaces
ALTER TABLE locations
  ALTER COLUMN code TYPE varchar(3)
  USING btrim(code)::varchar(3);

