-- Líneas de cada pedido (OrderItemEntity): una orden tiene muchas líneas.
--   * Sin tenant_id: a una línea solo se llega a través de su pedido, que ya filtra por tenant
--     (mismo criterio que menu_item_modifiers).
--   * product_name y unit_price son una foto del producto en el momento del pedido: si después cambia
--     su nombre o su precio, los pedidos y facturas antiguos siguen mostrando lo que se cobró.
--   * ON DELETE CASCADE: si se borra un pedido, se borran sus líneas. El producto, en cambio, no se puede
--     borrar si aparece en un pedido (los productos se borran de forma lógica, así que no debería pasar).
CREATE SEQUENCE IF NOT EXISTS orderitem_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE order_items (
	orderitem_id BIGINT        PRIMARY KEY,
	public_id    UUID          NOT NULL,
	order_id     BIGINT        NOT NULL REFERENCES orders (order_id) ON DELETE CASCADE,
	menuitems_id BIGINT        NOT NULL REFERENCES menu_items (menuitems_id),
	product_name VARCHAR(100)  NOT NULL,
	unit_price   DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
	quantity     INT           NOT NULL CHECK (quantity > 0),
	line_total   DECIMAL(10,2) NOT NULL CHECK (line_total >= 0),
	notes        VARCHAR(255),
	created_at   TIMESTAMP     NOT NULL,
	CONSTRAINT uk_order_items_public_id UNIQUE (public_id)
);

-- Para cargar las líneas de un pedido.
CREATE INDEX idx_order_items_order ON order_items (order_id);
-- Para saber en qué pedidos aparece un producto (informes de ventas).
CREATE INDEX idx_order_items_menu_item ON order_items (menuitems_id);
