-- Organizations (optional, can be a simple seed table)
CREATE TABLE IF NOT EXISTS organizations (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(150) NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Users table with org linkage
CREATE TABLE IF NOT EXISTS users (
  id             BIGSERIAL PRIMARY KEY,
  org_id         BIGINT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  email          VARCHAR(255) NOT NULL UNIQUE,
  password_hash  TEXT NOT NULL,
  full_name      VARCHAR(150),
  role           VARCHAR(30) NOT NULL DEFAULT 'USER',
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_users_org_email ON users(org_id, email);

-- Add org_id to existing customers table (adjust table name if different)
ALTER TABLE IF EXISTS customers
  ADD COLUMN IF NOT EXISTS org_id BIGINT;

-- If you already have rows, TEMP: backfill to an org, e.g., org_id = 1
UPDATE customers SET org_id = 1 WHERE org_id IS NULL;

-- Set NOT NULL + FK after backfill
ALTER TABLE customers
  ALTER COLUMN org_id SET NOT NULL;

ALTER TABLE customers
  ADD CONSTRAINT fk_customers_org
  FOREIGN KEY (org_id) REFERENCES organizations(id) ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_customers_org ON customers(org_id);
