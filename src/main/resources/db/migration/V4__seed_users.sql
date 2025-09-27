-- === V4: Seed admin/user accounts (dev) ===

-- bcrypt helpers (pgcrypto)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Make sure org 1 exists (safe if your V2 already added it)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM organizations WHERE id = 1) THEN
    INSERT INTO organizations (id, name) VALUES (1, 'Default Organization');
    PERFORM setval(
      pg_get_serial_sequence('organizations','id'),
      GREATEST((SELECT COALESCE(MAX(id),1) FROM organizations), 1),
      true
    );
  END IF;
END$$;

-- Admin (org 1)
INSERT INTO users (org_id, email, password_hash, full_name, role)
VALUES
  (1, 'admin@example.com',
     crypt('admin123', gen_salt('bf', 10)),
     'System Admin', 'ADMIN')
ON CONFLICT (org_id, email) DO NOTHING;

-- (Optional) Regular user (org 1)
INSERT INTO users (org_id, email, password_hash, full_name, role)
VALUES
  (1, 'user@example.com',
     crypt('user123', gen_salt('bf', 10)),
     'Demo User', 'USER')
ON CONFLICT (org_id, email) DO NOTHING;
