-- Un customer pasa a tener N direcciones: la FK se mueve de customers a addresses.
ALTER TABLE customers DROP COLUMN address_id;
-- Email único sin distinguir mayúsculas: Facturas@x.es y facturas@x.es son el mismo.
-- Los NULL no cuentan como duplicados (el email es opcional).
CREATE UNIQUE INDEX uk_customers_email_lower ON customers (lower(email));

ALTER TABLE addresses ADD COLUMN public_id   UUID    UNIQUE NOT NULL;
ALTER TABLE addresses ADD COLUMN customer_id BIGINT  NOT NULL REFERENCES customers(customer_id);
ALTER TABLE addresses ADD COLUMN active      BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_addresses_customer_id ON addresses(customer_id);
