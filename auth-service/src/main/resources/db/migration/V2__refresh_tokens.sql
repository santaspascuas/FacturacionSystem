-- Refresh tokens: de momento un unico dispositivo por usuario (UNIQUE en user_id).
-- Login sustituye la fila, refresh la rota en sitio y logout la marca como revocada.
CREATE SEQUENCE IF NOT EXISTS refresh_token_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS refresh_tokens (
    refresh_token_id  BIGINT       PRIMARY KEY,
    user_id           BIGINT       NOT NULL UNIQUE
        REFERENCES users(user_id) ON DELETE CASCADE,
    -- Solo se guarda el hash SHA-256 (64 caracteres hex), nunca el token en claro
    token_hash        VARCHAR(64)  NOT NULL UNIQUE,
    expires_at        TIMESTAMP    NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    revoked           BOOLEAN      NOT NULL DEFAULT FALSE,
    -- Bloqueo optimista: evita que dos refresh simultaneos roten el mismo token
    version           BIGINT       NOT NULL DEFAULT 0
);
