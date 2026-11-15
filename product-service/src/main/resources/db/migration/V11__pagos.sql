-- Cobros de los pedidos (PaymentEntity). Un pedido puede tener varios: cuenta dividida, o parte en efectivo
-- y parte con tarjeta. Mismo diseño que el resto de tablas: id de secuencia y public_id hacia fuera.
--   * Es un registro de caja: las filas no se modifican ni se borran, por eso no hay updated_at ni version.
--   * amount es lo que el cobro aplica al pedido. En efectivo, el cliente puede entregar más:
--     cash_received es lo entregado y change_given el cambio (cash_received - amount). Con tarjeta, los dos NULL.
--   * idempotency_key: el TPV puede reintentar un cobro; la misma clave en el mismo tenant no cobra dos veces.
--     PostgreSQL admite varios NULL en un UNIQUE (cobros sin clave).
--   * ON DELETE RESTRICT (por defecto): un pedido con cobros no se puede borrar (y los pedidos no se borran nunca).
-- Los tipos de pago tienen que coincidir con el enum PaymentType.
CREATE SEQUENCE IF NOT EXISTS payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE payments (
	payment_id      BIGINT        PRIMARY KEY,
	public_id       UUID          NOT NULL,
	tenant_id       UUID          NOT NULL,
	order_id        BIGINT        NOT NULL REFERENCES orders (order_id),
	amount          DECIMAL(10,2) NOT NULL CHECK (amount > 0),
	payment_type    VARCHAR(20)   NOT NULL CHECK (payment_type IN ('EFECTIVO', 'TARJETA')),
	cash_received   DECIMAL(10,2),
	change_given    DECIMAL(10,2),
	idempotency_key VARCHAR(100),
	created_at      TIMESTAMP     NOT NULL,
	CONSTRAINT uk_payments_public_id UNIQUE (public_id),
	CONSTRAINT uk_payments_tenant_idempotency UNIQUE (tenant_id, idempotency_key),
	-- Efectivo: lo entregado cubre el importe y el cambio cuadra. Tarjeta: sin datos de efectivo.
	CONSTRAINT ck_payments_efectivo CHECK (
		(payment_type = 'EFECTIVO' AND cash_received >= amount AND change_given = cash_received - amount)
		OR (payment_type <> 'EFECTIVO' AND cash_received IS NULL AND change_given IS NULL)
	)
);

-- Para listar los cobros de un pedido.
CREATE INDEX idx_payments_order ON payments (order_id);
-- Para el cierre de caja: los cobros de un tenant en un rango de fechas.
CREATE INDEX idx_payments_tenant_created ON payments (tenant_id, created_at);
