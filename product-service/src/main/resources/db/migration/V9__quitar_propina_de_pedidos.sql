-- El pedido deja de guardar la propina (Order.tipAmount desaparece). A partir de aquí:
--   total = subtotal + tax_amount   (base imponible + cuota de IVA = importe de las líneas, IVA incluido)
-- La propina nunca llegó a rellenarse (siempre 0.00), así que ningún total cambia al quitar la columna.
-- Su CHECK (tip_amount >= 0) se borra con ella.
ALTER TABLE orders DROP COLUMN tip_amount;
