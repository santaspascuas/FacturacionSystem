CREATE SEQUENCE IF NOT EXISTS usertpv_id_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS userstpv (
	usertpv_id    BIGINT       PRIMARY KEY,
	tenant_id     UUID         NOT NULL,
	username      VARCHAR(100) NOT NULL,
	hash_password VARCHAR(100) NOT NULL,
	role_tpv      VARCHAR(100) NOT NULL,
	created_at    TIMESTAMP    NOT NULL,
	updated_at    TIMESTAMP    NOT NULL,
	version       BIGINT       NOT NULL DEFAULT 0,
	CONSTRAINT uk_userstpv_tenant_username UNIQUE (tenant_id, username)
);


CREATE TABLE menu_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);