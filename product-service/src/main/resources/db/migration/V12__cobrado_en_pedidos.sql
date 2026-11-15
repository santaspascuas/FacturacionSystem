-- Lo cobrado de cada pedido (Order.paidAmount / paidAt). Un pedido se cobra con uno o varios pagos (payments, V11)
-- y pasa a CIERRAYPAGA cuando lo cobrado llega al total. Desde ahora CIERRAYPAGA solo lo pone el cobro.
--   * paid_amount: suma de los cobros. Nunca negativo ni por encima del total (no se cobra de más).
--   * paid_at: cuándo quedó pagado del todo. NULL mientras quede algo pendiente.
ALTER TABLE orders ADD COLUMN paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE orders ADD COLUMN paid_at TIMESTAMP;

-- Los pedidos que ya estaban en CIERRAYPAGA se cerraron a mano, sin cobros registrados (antes no existían).
-- Se dan por pagados enteros para que cuadren con la regla nueva; como hora de pago, la de su última modificación.
UPDATE orders SET paid_amount = total, paid_at = updated_at WHERE status = 'CIERRAYPAGA';

ALTER TABLE orders ADD CONSTRAINT ck_orders_paid_amount CHECK (paid_amount >= 0 AND paid_amount <= total);
