-- active = dirección fiscal (la que saldrá en las facturas). Como máximo una por customer.

-- 1) Datos existentes: si un customer tiene varias activas, se queda como fiscal la más reciente
--    (misma regla que el alta: "siempre la última").
UPDATE addresses a
SET active = FALSE
WHERE a.active
  AND a.address_id <> (SELECT MAX(b.address_id) FROM addresses b WHERE b.customer_id = a.customer_id AND b.active);

-- 2) Índice único parcial: solo cuenta las filas con active = TRUE.
CREATE UNIQUE INDEX uk_addresses_customer_fiscal ON addresses (customer_id) WHERE active;
