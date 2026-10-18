CREATE SEQUENCE IF NOT EXISTS tenant_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS tenants (
    tenant_id   BIGINT       PRIMARY KEY,
    public_id   UUID         UNIQUE NOT NULL,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);


CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT PRIMARY KEY,
	public_id UUID UNIQUE NOT NULL,
	tenant_id BIGINT NOT NULL REFERENCES tenants(tenant_id),
	email VARCHAR(255) UNIQUE NOT NULL,
	password_hash VARCHAR(255)  NOT NULL,
	role VARCHAR(20)  NOT NULL,
	active BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    failed_attempts INT NOT NULL DEFAULT 0,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    lock_time TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_role_active ON users(role, active);
