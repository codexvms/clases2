CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_rol FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_email ON usuario (email);

INSERT INTO rol (nombre)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'ROLE_ADMIN');

INSERT INTO rol (nombre)
SELECT 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'ROLE_USER');

INSERT INTO usuario (nombre, email, password_hash, enabled)
SELECT 'Administrador', 'admin@binexus.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiitC7hoKQfM8TpQqFD4e7yW4Kc9e', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@binexus.com');

INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuario u
JOIN rol r ON r.nombre = 'ROLE_ADMIN'
WHERE u.email = 'admin@binexus.com'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_rol ur WHERE ur.usuario_id = u.id AND ur.rol_id = r.id
);
