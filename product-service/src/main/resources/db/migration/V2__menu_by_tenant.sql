-- V1 creó menu_categories sin tenant y con id UUID, y todavía no se usaba. Se rehace con el diseño de userstpv:
-- id de secuencia, public_id hacia fuera, version para el bloqueo optimista y nombre único dentro de cada tenant.
DROP TABLE menu_categories;

CREATE SEQUENCE IF NOT EXISTS category_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE menu_categories (
	category_id   BIGINT       PRIMARY KEY,
	public_id     UUID         NOT NULL,
	tenant_id     UUID         NOT NULL,
	name          VARCHAR(100) NOT NULL,
	description   TEXT,
	display_order INT          NOT NULL DEFAULT 0,
	active        BOOLEAN      NOT NULL DEFAULT TRUE,
	created_at    TIMESTAMP    NOT NULL,
	updated_at    TIMESTAMP    NOT NULL,
	version       BIGINT       NOT NULL DEFAULT 0,
	CONSTRAINT uk_menu_categories_public_id UNIQUE (public_id),
	CONSTRAINT uk_menu_categories_tenant_name UNIQUE (tenant_id, name)
);

-- Los productos de cada categoría, con el mismo diseño que menu_categories (MenuItemEntity).
CREATE SEQUENCE IF NOT EXISTS menuitems_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE menu_items (
	menuitems_id BIGINT        PRIMARY KEY,
	public_id    UUID          NOT NULL,
	tenant_id    UUID          NOT NULL,
	category_id  BIGINT        NOT NULL REFERENCES menu_categories (category_id),
	name         VARCHAR(100)  NOT NULL,
	description  TEXT,
	base_price   DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
	active       BOOLEAN       NOT NULL DEFAULT TRUE,
	image_url    VARCHAR(500),
	created_at   TIMESTAMP     NOT NULL,
	updated_at   TIMESTAMP     NOT NULL,
	version      BIGINT        NOT NULL DEFAULT 0,
	CONSTRAINT uk_menu_items_public_id UNIQUE (public_id)
);

-- Para listar la carta de un tenant y los productos de una categoría.
CREATE INDEX idx_menu_items_tenant ON menu_items (tenant_id);
CREATE INDEX idx_menu_items_category ON menu_items (category_id);


-- Modificadores de cada tenant (p. ej. "Sin cebolla", "Extra de queso"), con el mismo diseño que menu_items.
-- price_delta puede ser negativo: quitar un ingrediente puede abaratar el producto.
CREATE SEQUENCE IF NOT EXISTS modificador_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE modifiers (
	modificador_id BIGINT        PRIMARY KEY,
	public_id      UUID          NOT NULL,
	tenant_id      UUID          NOT NULL,
	name           VARCHAR(100)  NOT NULL,
	modifier_type  VARCHAR(20)   NOT NULL CHECK (modifier_type IN ('ANADIR', 'ELIMINAR', 'SUSTITUIR')),
	price_delta    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
	active         BOOLEAN       NOT NULL DEFAULT TRUE,
	created_at     TIMESTAMP     NOT NULL,
	updated_at     TIMESTAMP     NOT NULL,
	version        BIGINT        NOT NULL DEFAULT 0,
	CONSTRAINT uk_modifiers_public_id UNIQUE (public_id),
	CONSTRAINT uk_modifiers_tenant_name UNIQUE (tenant_id, name)
);

-- Qué modificadores admite cada producto (muchos a muchos). El producto y el modificador tienen que ser
-- del mismo tenant: la BD no lo puede comprobar con estas claves, así que lo comprueba el servicio.
-- Los productos y modificadores se borran de forma lógica, así que el CASCADE solo actúa en un borrado físico.
CREATE SEQUENCE IF NOT EXISTS menu_item_modifier_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE menu_item_modifiers (
	menu_item_modifier_id BIGINT  PRIMARY KEY,
	menuitems_id          BIGINT  NOT NULL REFERENCES menu_items (menuitems_id) ON DELETE CASCADE,
	modificador_id        BIGINT  NOT NULL REFERENCES modifiers (modificador_id) ON DELETE CASCADE,
	max_selections        INT     NOT NULL DEFAULT 1 CHECK (max_selections >= 1),
	required              BOOLEAN NOT NULL DEFAULT FALSE,
	-- Un modificador no se asocia dos veces al mismo producto.
	CONSTRAINT uk_menu_item_modifiers_item_modifier UNIQUE (menuitems_id, modificador_id)
);

-- El UNIQUE ya sirve para buscar por producto; este índice, para buscar por modificador.
CREATE INDEX idx_menu_item_modifiers_modifier ON menu_item_modifiers (modificador_id);













