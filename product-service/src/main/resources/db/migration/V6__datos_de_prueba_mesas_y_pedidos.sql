-- Datos de prueba de mesas y pedidos, para probar los endpoints de /api/table y /api/order.
-- Usa los tenants, productos y modificadores de la V3.
--
-- ATENCIÓN: igual que la V3, al estar en db/migration Flyway lo aplica en TODAS las BD donde arranque el servicio,
-- también en producción, y una vez aplicado no se puede editar. Para quitar estos datos hará falta otra migración.
--
-- Reglas: ids de las secuencias (nextval), claves foráneas resueltas por public_id, fechas en UTC
-- (el servicio guarda con Clock.systemUTC()). publicIds fáciles de reconocer:
--   dddddddd-… mesas, eeeeeeee-… pedidos.
--
-- Restaurante A (11111111-…):
--   Mesas 1 y 2 OCUPADAS (tienen pedido abierto), 3 a 6 DISPONIBLES.
--   Pedido 1  mesa 1  ENVIADO           Clásica x2 ("poco hecha") + Coca-Cola x2                24,80
--   Pedido 2  mesa 2  PREPARANDOCOMIDA  Doble x1 + Clásica x1 ("sin cebolla") + Cerveza x2      28,40
--   Pedido 3  LLEVAR  LISTO             Clásica x1 + Agua x1                                    11,70
--   Pedido 4  mesa 3  CIERRAYPAGA       Tarta de queso x2 + Brownie x1 (historial: pagado)       13,00
--   Pedido 5  LLEVAR  CANCELADO         Doble x1 (historial: anulado)                           12,50
-- Restaurante B (22222222-…):
--   Mesa 1 OCUPADA; pedido 1 mesa 1 ENVIADO: Café con leche x2                                  3,00


-- ===================== Mesas =====================
INSERT INTO tablerestaurante (table_id, public_id, tenant_id, table_number, capacity, zone, status, created_at, updated_at)
SELECT nextval('table_id_seq'), m.public_id::uuid, m.tenant_id::uuid, m.numero, m.capacidad, m.zona, m.estado,
	now() AT TIME ZONE 'UTC', now() AT TIME ZONE 'UTC'
FROM (VALUES
	('dddddddd-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 1, 4, 'SALON',   'OCUPADA'),
	('dddddddd-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 2, 4, 'SALON',   'OCUPADA'),
	('dddddddd-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 3, 2, 'SALON',   'DISPONIBLE'),
	('dddddddd-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 4, 6, 'TERRAZA', 'DISPONIBLE'),
	('dddddddd-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 5, 2, 'TERRAZA', 'DISPONIBLE'),
	('dddddddd-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111', 6, 1, 'BARRA',   'DISPONIBLE'),
	('dddddddd-0000-0000-0000-000000000007', '22222222-2222-2222-2222-222222222222', 1, 4, 'SALON',   'OCUPADA'),
	('dddddddd-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222222', 2, 2, 'TERRAZA', 'DISPONIBLE')
) AS m(public_id, tenant_id, numero, capacidad, zona, estado);


-- ===================== Pedidos =====================
-- Los importes cuadran con las líneas de abajo (subtotal = suma de line_total; total = subtotal, sin IVA ni propina).
-- created_at escalonado para que los listados salgan en un orden reconocible.
INSERT INTO orders (order_id, public_id, tenant_id, order_number, order_type, status, table_id, num_personas,
	subtotal, total, created_at, updated_at, cooked_at)
SELECT nextval('order_id_seq'), p.public_id::uuid, p.tenant_id::uuid, p.numero, p.tipo, p.estado, t.table_id, p.personas,
	p.importe, p.importe,
	(now() AT TIME ZONE 'UTC') - make_interval(mins => p.hace_min),
	(now() AT TIME ZONE 'UTC') - make_interval(mins => p.hace_min),
	CASE WHEN p.estado IN ('LISTO', 'CIERRAYPAGA') THEN (now() AT TIME ZONE 'UTC') - make_interval(mins => p.hace_min - 15) END
FROM (VALUES
	('eeeeeeee-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 1, 'COMERAQUI', 'ENVIADO',          'dddddddd-0000-0000-0000-000000000001', 2,    24.80, 10),
	('eeeeeeee-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 2, 'COMERAQUI', 'PREPARANDOCOMIDA', 'dddddddd-0000-0000-0000-000000000002', 4,    28.40, 30),
	('eeeeeeee-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 3, 'LLEVAR',    'LISTO',            NULL,                                   NULL, 11.70, 45),
	('eeeeeeee-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 4, 'COMERAQUI', 'CIERRAYPAGA',      'dddddddd-0000-0000-0000-000000000003', 2,    13.00, 120),
	('eeeeeeee-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 5, 'LLEVAR',    'CANCELADO',        NULL,                                   NULL, 12.50, 180),
	('eeeeeeee-0000-0000-0000-000000000006', '22222222-2222-2222-2222-222222222222', 1, 'COMERAQUI', 'ENVIADO',          'dddddddd-0000-0000-0000-000000000007', 2,    3.00,  5)
) AS p(public_id, tenant_id, numero, tipo, estado, mesa_public_id, personas, importe, hace_min)
LEFT JOIN tablerestaurante t ON t.public_id = p.mesa_public_id::uuid;


-- ===================== Líneas de los pedidos =====================
-- Nombre y precio copiados del producto de la V3 (foto del momento), como hace RegistrarOrderService.
-- La columna n fija el orden: la subconsulta ordenada hace que los ids de secuencia se asignen en el orden en que
-- están escritas las líneas (un JOIN no conserva el orden del VALUES), y OrderEntity las devuelve por ese id.
INSERT INTO order_items (orderitem_id, public_id, order_id, menuitems_id, product_name, unit_price, quantity, line_total,
	notes, created_at)
SELECT nextval('orderitem_id_seq'), gen_random_uuid(), x.order_id, x.menuitems_id, x.name, x.base_price, x.cantidad,
	x.base_price * x.cantidad, x.notas, x.created_at
FROM (
	SELECT o.order_id, m.menuitems_id, m.name, m.base_price, l.cantidad, l.notas, o.created_at, l.n
	FROM (VALUES
		(1,  'eeeeeeee-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 2, 'poco hecha'),
		(2,  'eeeeeeee-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000005', 2, NULL),
		(3,  'eeeeeeee-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002', 1, NULL),
		(4,  'eeeeeeee-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000001', 1, 'sin cebolla'),
		(5,  'eeeeeeee-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000007', 2, NULL),
		(6,  'eeeeeeee-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000001', 1, NULL),
		(7,  'eeeeeeee-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000006', 1, NULL),
		(8,  'eeeeeeee-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000008', 2, NULL),
		(9,  'eeeeeeee-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000009', 1, NULL),
		(10, 'eeeeeeee-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000002', 1, NULL),
		(11, 'eeeeeeee-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000010', 2, NULL)
	) AS l(n, order_public_id, item_public_id, cantidad, notas)
	JOIN orders o ON o.public_id = l.order_public_id::uuid
	JOIN menu_items m ON m.public_id = l.item_public_id::uuid
	ORDER BY l.n
) x;
