-- === V2: Seed default org and sample customers ===

-- Ensure a default org exists with id = 1 (useful for local/dev)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM organizations WHERE id = 1) THEN
    INSERT INTO organizations (id, name)
    VALUES (1, 'Default Organization');
    -- keep the organizations.id sequence in sync (safe even if already ahead)
    PERFORM setval(
      pg_get_serial_sequence('organizations','id'),
      GREATEST((SELECT COALESCE(MAX(id),1) FROM organizations), 1),
      true
    );
  END IF;
END$$;

-- Seed customers into org 1 (skip if emails already exist)
INSERT INTO customers (org_id, name, email, phone)
SELECT 1, 'Alice Wanjiru', 'alice@example.com', '0712000001'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'alice@example.com');

INSERT INTO customers (org_id, name, email, phone)
SELECT 1, 'Brian Otieno', 'brian@example.com', '0712000002'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'brian@example.com');

