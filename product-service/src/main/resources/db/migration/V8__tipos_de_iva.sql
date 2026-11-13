-- Tipos de IVA con su fecha de entrada en vigor (TaxRateEntity / JpaTipoImpositivoAdapter).
-- Cada pedido nuevo toma el tipo vigente en su momento y lo guarda en orders.tax_rate (V7).
--
-- CÓMO CAMBIAR EL IVA: insertar una fila con el tipo nuevo y su fecha, con una migración nueva (puede subirse
-- semanas antes) o con un INSERT. No se editan ni se borran filas: así queda el historial.
--
-- ATENCIÓN, valid_from va en UTC (como todas las fechas del servicio, Clock.systemUTC()). La ley fija la entrada
-- en vigor a las 00:00 hora peninsular, que en UTC es la víspera:
--   00:00 de un 1 de enero en Madrid (invierno, UTC+1) -> 'AAAA-12-31 23:00:00' del año anterior
--   00:00 de un 1 de julio en Madrid (verano, UTC+2)   -> 'AAAA-06-30 22:00:00'
-- Ejemplo: INSERT INTO tax_rates (tax_rate_id, rate, valid_from) VALUES (nextval('tax_rate_id_seq'), 12.00, '2027-12-31 23:00:00');
CREATE SEQUENCE IF NOT EXISTS tax_rate_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE tax_rates (
	tax_rate_id BIGINT       PRIMARY KEY,
	rate        DECIMAL(5,2) NOT NULL CHECK (rate >= 0 AND rate < 100),
	valid_from  TIMESTAMP    NOT NULL,
	-- Dos tipos no pueden empezar a la vez. El índice del UNIQUE sirve también para buscar el vigente.
	CONSTRAINT uk_tax_rates_valid_from UNIQUE (valid_from)
);

-- IVA reducido de hostelería, 10 % desde el 1-9-2012 (Real Decreto-ley 20/2012): 00:00 en Madrid = 22:00 UTC del 31-8.
INSERT INTO tax_rates (tax_rate_id, rate, valid_from)
VALUES (nextval('tax_rate_id_seq'), 10.00, '2012-08-31 22:00:00');
