-- Make email unique per org for both users and customers
DO $$
DECLARE
  r RECORD;
BEGIN
  -- Drop any existing UNIQUE constraint on users.email (name is unknown)
  FOR r IN
    SELECT conname
    FROM   pg_constraint
    WHERE  conrelid = 'users'::regclass
       AND contype  = 'u'
       AND conname  LIKE '%email%'
  LOOP
    EXECUTE format('ALTER TABLE users DROP CONSTRAINT %I', r.conname);
  END LOOP;

  -- Drop any existing UNIQUE constraint on customers.email
  FOR r IN
    SELECT conname
    FROM   pg_constraint
    WHERE  conrelid = 'customers'::regclass
       AND contype  = 'u'
       AND conname  LIKE '%email%'
  LOOP
    EXECUTE format('ALTER TABLE customers DROP CONSTRAINT %I', r.conname);
  END LOOP;

  -- Add composite unique constraints (idempotent-ish via exception guard)
  BEGIN
    ALTER TABLE users ADD CONSTRAINT uq_users_org_email UNIQUE (org_id, email);
  EXCEPTION WHEN duplicate_object THEN
    -- already exists
  END;

  BEGIN
    ALTER TABLE customers ADD CONSTRAINT uq_customers_org_email UNIQUE (org_id, email);
  EXCEPTION WHEN duplicate_object THEN
  END;
END$$;
