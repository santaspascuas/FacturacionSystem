-- Al borrar un customer, Postgres borra sus direcciones (antes la FK impedía el borrado: NO ACTION).
ALTER TABLE addresses DROP CONSTRAINT addresses_customer_id_fkey;
ALTER TABLE addresses ADD CONSTRAINT addresses_customer_id_fkey
	FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE;
