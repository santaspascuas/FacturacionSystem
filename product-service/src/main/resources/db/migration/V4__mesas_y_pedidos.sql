-- Mesas de cada tenant (TablesEntity / TableRestautant), con el mismo diseño que el resto de tablas:
-- id de secuencia, public_id hacia fuera, version para el bloqueo optimista y número de mesa único dentro de cada tenant.
-- Las zonas y los estados tienen que coincidir con los enums TableZone y TableStatus.
-- Todavía sin camarero asignado ni borrado lógico (active): se añadirán con una migración nueva si hacen falta.
CREATE SEQUENCE IF NOT EXISTS table_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE tablerestaurante (
	table_id     BIGINT      PRIMARY KEY,
	public_id    UUID        NOT NULL,
	tenant_id    UUID        NOT NULL,
	table_number INT         NOT NULL CHECK (table_number > 0),
	capacity     INT         NOT NULL CHECK (capacity > 0),
	zone         VARCHAR(20) NOT NULL CHECK (zone IN ('SALON', 'TERRAZA', 'BARRA')),
	status       VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
	             CHECK (status IN ('DISPONIBLE', 'OCUPADA', 'RESERVADA', 'LIMPIANDO')),
	created_at   TIMESTAMP   NOT NULL,
	updated_at   TIMESTAMP   NOT NULL,
	version      BIGINT      NOT NULL DEFAULT 0,
	CONSTRAINT uk_tablerestaurante_public_id UNIQUE (public_id),
	CONSTRAINT uk_tablerestaurante_tenant_number UNIQUE (tenant_id, table_number)
);


-- Pedidos de cada tenant (OrderEntity). Los tipos, estados y estados de sincronización tienen que coincidir con
-- los enums OrderType, OrderStatus y SyncStatus.
--   * table_id es opcional (un pedido para LLEVAR no tiene mesa) y no es UNIQUE: una mesa tiene muchos pedidos.
--     Que la mesa sea del mismo tenant que el pedido lo comprobará el servicio (la BD no puede con estas claves).
--   * Los importes son una foto del momento del pedido y no pueden ser negativos.
--   * cooked_at es null hasta que cocina termina el pedido.
--   * idempotency_key: el TPV puede reintentar el envío de un pedido (p. ej. tras estar sin conexión);
--     la misma clave en el mismo tenant hace que no se cree dos veces. PostgreSQL admite varios NULL en un UNIQUE.
CREATE SEQUENCE IF NOT EXISTS order_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE orders (
	order_id         BIGINT        PRIMARY KEY,
	public_id        UUID          NOT NULL,
	tenant_id        UUID          NOT NULL,
	order_number     INT           NOT NULL CHECK (order_number > 0),
	order_type       VARCHAR(20)   NOT NULL CHECK (order_type IN ('COMERAQUI', 'LLEVAR')),
	status           VARCHAR(20)   NOT NULL
	                 CHECK (status IN ('ENVIADO', 'COMANDALANZADA', 'PREPARANDOCOMIDA', 'LISTO', 'CIERRAYPAGA', 'CANCELADO')),
	table_id         BIGINT        REFERENCES tablerestaurante (table_id),
	num_personas     INT           CHECK (num_personas > 0),
	subtotal         DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (subtotal >= 0),
	tax_amount       DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (tax_amount >= 0),
	tip_amount       DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (tip_amount >= 0),
	total            DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (total >= 0),
	created_at       TIMESTAMP     NOT NULL,
	updated_at       TIMESTAMP     NOT NULL,
	cooked_at        TIMESTAMP,
	version          BIGINT        NOT NULL DEFAULT 0,
	idempotency_key  VARCHAR(100),
	client_timestamp TIMESTAMP,
	sync_status      VARCHAR(20)   NOT NULL DEFAULT 'SYNCED' CHECK (sync_status IN ('SYNCED', 'PENDING', 'CONFLICT')),
	CONSTRAINT uk_orders_public_id UNIQUE (public_id),
	CONSTRAINT uk_order_tenant_number UNIQUE (tenant_id, order_number),
	CONSTRAINT uk_orders_tenant_idempotency UNIQUE (tenant_id, idempotency_key)
);

-- Una mesa, una orden: cada mesa solo puede tener UN pedido abierto a la vez. Es un índice único parcial:
-- solo cuenta los pedidos que no están cerrados (CIERRAYPAGA) ni cancelados, así que los pedidos antiguos
-- de la mesa se conservan como historial (facturas) y no impiden abrir uno nuevo.
-- Los pedidos para LLEVAR (table_id null) no cuentan: PostgreSQL no compara los NULL en un UNIQUE.
-- También sirve para buscar los pedidos de una mesa (y para que borrar una mesa compruebe rápido si tiene pedidos).
CREATE UNIQUE INDEX uk_orders_mesa_abierta ON orders (table_id)
	WHERE status NOT IN ('CIERRAYPAGA', 'CANCELADO');
CREATE INDEX idx_orders_table ON orders (table_id);
-- Para listar los pedidos de un tenant por estado (p. ej. los que están en cocina).
CREATE INDEX idx_orders_tenant_status ON orders (tenant_id, status);
