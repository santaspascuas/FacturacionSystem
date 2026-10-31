CREATE SEQUENCE IF NOT EXISTS address_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS customer_id_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS addresses (
	address_id  BIGINT       PRIMARY KEY,
	street      VARCHAR(150) NOT NULL,
	postal_code VARCHAR(10)  NOT NULL,
	city        VARCHAR(100) NOT NULL,
	province    VARCHAR(100),
	country     VARCHAR(2)   NOT NULL
);


CREATE TABLE IF NOT EXISTS customers (
	customer_id  BIGINT       PRIMARY KEY,
	tenant_id    UUID         UNIQUE NOT NULL,
	razon_social VARCHAR(120) NOT NULL,
	nif          VARCHAR(9)   NOT NULL,
	email        VARCHAR(255),
	address_id   BIGINT       UNIQUE NOT NULL REFERENCES addresses(address_id),
	created_at   TIMESTAMP    NOT NULL,
	updated_at   TIMESTAMP    NOT NULL
);
