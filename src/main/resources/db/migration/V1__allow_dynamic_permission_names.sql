-- Permission names are managed dynamically through the role permission API.
-- Older database setups may have created a check constraint that only allows
-- a fixed set of names; remove it so newly added permissions can be stored.
ALTER TABLE permissions
    DROP CONSTRAINT IF EXISTS permissions_name_check;
