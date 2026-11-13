-- Datos de prueba para arrancar el servicio con la carta de un restaurante ya creada y probar los endpoints.
--
-- ATENCIÓN: al estar en db/migration, Flyway aplica este script en TODAS las BD donde arranque el servicio,
-- también en producción, y una vez aplicado no se puede editar (cambiaría su checksum). Para quitar estos
-- datos más adelante hará falta una migración nueva que los borre.
--
-- Reglas que sigue el script:
--   * Los ids internos salen de las secuencias (nextval). Si se pusieran a mano, la secuencia no avanzaría y
--     el siguiente alta desde la API chocaría con la clave primaria (Hibernate pide el id a la secuencia).
--   * Las claves foráneas se resuelven por public_id, que es fijo para poder copiarlo en las llamadas.
--   * publicIds fáciles de reconocer: aaaaaaaa-… categorías, bbbbbbbb-… productos, cccccccc-… modificadores.
--
-- Tenants:
--   11111111-1111-1111-1111-111111111111  Restaurante A (la carta completa)
--   22222222-2222-2222-2222-222222222222  Restaurante B (sirve para comprobar que un tenant no ve lo de otro)
--
-- Usuarios TPV (contraseña de todos: tpv1234, guardada con BCrypt como hace RegisterUserTpvService):
--   Restaurante A: camarero1 (Trabajador), encargado (Admin)
--   Restaurante B: camarero1 (Trabajador)  -> mismo username en otro tenant


-- ===================== Usuarios TPV =====================
INSERT INTO userstpv (usertpv_id, tenant_id, username, hash_password, role_tpv, created_at, updated_at) VALUES
	(nextval('usertpv_id_seq'), '11111111-1111-1111-1111-111111111111', 'camarero1',
		'$2a$10$fsZWQM.lsgyssNvZeK8zDu.7g3Sw1CCpuzCODDb8FQb.C.8wFjFZu', 'Trabajador', LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('usertpv_id_seq'), '11111111-1111-1111-1111-111111111111', 'encargado',
		'$2a$10$fsZWQM.lsgyssNvZeK8zDu.7g3Sw1CCpuzCODDb8FQb.C.8wFjFZu', 'Admin', LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('usertpv_id_seq'), '22222222-2222-2222-2222-222222222222', 'camarero1',
		'$2a$10$fsZWQM.lsgyssNvZeK8zDu.7g3Sw1CCpuzCODDb8FQb.C.8wFjFZu', 'Trabajador', LOCALTIMESTAMP, LOCALTIMESTAMP);


-- ===================== Categorías =====================
-- "Entrantes" está inactiva: no sale en GET /api/category/all y se puede reactivar con PUT.
INSERT INTO menu_categories (category_id, public_id, tenant_id, name, display_order, active, created_at, updated_at) VALUES
	(nextval('category_id_seq'), 'aaaaaaaa-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Hamburguesas', 1, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('category_id_seq'), 'aaaaaaaa-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Bebidas',      2, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('category_id_seq'), 'aaaaaaaa-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'Postres',      3, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('category_id_seq'), 'aaaaaaaa-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'Entrantes',    4, FALSE, LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('category_id_seq'), 'aaaaaaaa-0000-0000-0000-000000000005', '22222222-2222-2222-2222-222222222222', 'Cafés',        1, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP);


-- ===================== Productos =====================
-- "Hamburguesa BBQ" está eliminada (active = FALSE): no sale en los listados, DELETE da 404 y PUT la reactiva.
INSERT INTO menu_items (menuitems_id, public_id, tenant_id, category_id, name, description, base_price, active, image_url, created_at, updated_at)
SELECT nextval('menuitems_id_seq'), p.public_id::uuid, c.tenant_id, c.category_id, p.name, p.description, p.base_price, p.active, NULL,
	LOCALTIMESTAMP, LOCALTIMESTAMP
FROM (VALUES
	('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'Hamburguesa Clásica', 'Ternera, lechuga, tomate y cebolla', 9.90,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000001', 'Hamburguesa Doble',   'Doble de ternera y doble de queso',  12.50, TRUE),
	('bbbbbbbb-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000001', 'Hamburguesa Vegana',  'Hamburguesa vegetal',                11.00, TRUE),
	('bbbbbbbb-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000001', 'Hamburguesa BBQ',     'Con salsa barbacoa',                 11.50, FALSE),
	('bbbbbbbb-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000002', 'Coca-Cola',           NULL,                                 2.50,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000006', 'aaaaaaaa-0000-0000-0000-000000000002', 'Agua mineral',        NULL,                                 1.80,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000007', 'aaaaaaaa-0000-0000-0000-000000000002', 'Cerveza',             NULL,                                 3.00,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000008', 'aaaaaaaa-0000-0000-0000-000000000003', 'Tarta de queso',      NULL,                                 4.50,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000009', 'aaaaaaaa-0000-0000-0000-000000000003', 'Brownie',             'Con helado de vainilla',             4.00,  TRUE),
	('bbbbbbbb-0000-0000-0000-000000000010', 'aaaaaaaa-0000-0000-0000-000000000005', 'Café con leche',      NULL,                                 1.50,  TRUE)
) AS p(public_id, category_public_id, name, description, base_price, active)
JOIN menu_categories c ON c.public_id = p.category_public_id::uuid;


-- ===================== Modificadores =====================
-- "Extra de jalapeños" está eliminado: no sale en GET /api/modifier/all ni en los modificadores de la Clásica,
-- aunque sigue asociado a ella (se ve el efecto del borrado lógico).
INSERT INTO modifiers (modificador_id, public_id, tenant_id, name, modifier_type, price_delta, active, created_at, updated_at) VALUES
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Extra de queso',     'ANADIR',    1.50, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Extra de bacon',     'ANADIR',    2.00, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'Sin cebolla',        'ELIMINAR',  0.00, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'Sin queso',          'ELIMINAR', -0.50, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 'Pan sin gluten',     'SUSTITUIR', 1.00, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111', 'Sin hielo',          'ELIMINAR',  0.00, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111', 'Extra de jalapeños', 'ANADIR',    0.80, FALSE, LOCALTIMESTAMP, LOCALTIMESTAMP),
	(nextval('modificador_id_seq'), 'cccccccc-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222222', 'Leche de avena',     'SUSTITUIR', 0.30, TRUE,  LOCALTIMESTAMP, LOCALTIMESTAMP);


-- ===================== Modificadores de cada producto =====================
-- Sin asociar a propósito, para probar POST: "Sin queso" en la Clásica y nada en la Vegana ni en los postres.
INSERT INTO menu_item_modifiers (menu_item_modifier_id, menuitems_id, modificador_id, max_selections, required)
SELECT nextval('menu_item_modifier_id_seq'), i.menuitems_id, m.modificador_id, a.max_selections, a.required
FROM (VALUES
	('bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 2, FALSE),  -- Clásica + Extra de queso (hasta 2)
	('bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000002', 1, FALSE),  -- Clásica + Extra de bacon
	('bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000003', 1, FALSE),  -- Clásica + Sin cebolla
	('bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000005', 1, FALSE),  -- Clásica + Pan sin gluten
	('bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000007', 1, FALSE),  -- Clásica + Jalapeños (eliminado: no se lista)
	('bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002', 2, FALSE),  -- Doble + Extra de bacon (hasta 2)
	('bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000004', 1, FALSE),  -- Doble + Sin queso
	('bbbbbbbb-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000006', 1, FALSE),  -- Coca-Cola + Sin hielo
	('bbbbbbbb-0000-0000-0000-000000000010', 'cccccccc-0000-0000-0000-000000000008', 1, FALSE)   -- Café con leche (B) + Leche de avena
) AS a(item_public_id, modifier_public_id, max_selections, required)
JOIN menu_items i ON i.public_id = a.item_public_id::uuid
JOIN modifiers m ON m.public_id = a.modifier_public_id::uuid;
