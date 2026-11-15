-- 1. Idempotencia al añadir platos (OrderItem.idempotencyKey / OrderItemEntity.idempotencyKey).
--    Cada envío del TPV ("añade estos platos") lleva una clave; todas las líneas de ese envío la guardan.
--    Si el TPV reintenta el envío (p. ej. tras quedarse sin conexión), el pedido ya tiene líneas con esa clave
--    y no las vuelve a añadir. No es UNIQUE porque varias líneas comparten clave; dos reintentos simultáneos
--    los frena el bloqueo optimista de orders (version). Las líneas existentes se quedan sin clave (NULL).
--    Sin índice: la comprobación se hace sobre las líneas del pedido, que ya están cargadas en memoria.
ALTER TABLE order_items ADD COLUMN idempotency_key VARCHAR(100);

-- 2. El CHECK de orders.status (V4) admitía 'COMANDALANZADA', que no existe en el enum OrderStatus.
--    Se rehace con exactamente los valores del enum. PostgreSQL llamó al CHECK de la V4 orders_status_check.
ALTER TABLE orders DROP CONSTRAINT orders_status_check;
ALTER TABLE orders ADD CONSTRAINT ck_orders_status
	CHECK (status IN ('ENVIADO', 'PREPARANDOCOMIDA', 'LISTO', 'CIERRAYPAGA', 'CANCELADO'));
