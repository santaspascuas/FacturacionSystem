-- Bloqueo optimista: Hibernate añade "WHERE version = ?" al UPDATE y la incrementa.
-- Si otra petición guardó antes, el UPDATE no encuentra la fila y se rechaza (409) en lugar de pisar sus cambios.
ALTER TABLE customers ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
