-- Tipo de IVA en cada pedido (Order.taxRate / OrderEntity.taxRate).
--   * Es una foto del tipo vigente cuando se abrió el pedido: si la ley lo cambia, los pedidos y facturas
--     antiguos conservan el que se aplicó. El tipo vigente lo da TipoImpositivoPort (hoy, facturacion.iva.tipo).
--   * Los precios de la carta llevan el IVA incluido. Desde esta versión:
--       subtotal   = base imponible = importe de las líneas / (1 + tax_rate/100), redondeada a céntimos
--       tax_amount = cuota          = importe de las líneas - subtotal
--       total      = subtotal + tax_amount + tip_amount   (el total no cambia: la propina no lleva IVA)

-- Los pedidos que ya existen se crearon con el 10 % de hostelería. El DEFAULT solo sirve para rellenarlos:
-- se quita después para que un pedido nuevo no pueda guardarse sin que el servicio indique su tipo.
ALTER TABLE orders ADD COLUMN tax_rate DECIMAL(5,2) NOT NULL DEFAULT 10.00
	CONSTRAINT ck_orders_tax_rate CHECK (tax_rate >= 0 AND tax_rate < 100);
ALTER TABLE orders ALTER COLUMN tax_rate DROP DEFAULT;

-- Hasta ahora la cuota nunca se calculaba (tax_amount = 0) y subtotal guardaba el importe de las líneas con IVA.
-- Se separa ese importe en base + cuota con la misma fórmula que Order.recalcularTotales. total no se toca:
-- base + cuota = el subtotal anterior. ROUND de PostgreSQL redondea la mitad hacia arriba, igual que HALF_UP.
UPDATE orders
SET subtotal   = ROUND(subtotal / (1 + tax_rate / 100), 2),
    tax_amount = subtotal - ROUND(subtotal / (1 + tax_rate / 100), 2)
WHERE tax_amount = 0;
