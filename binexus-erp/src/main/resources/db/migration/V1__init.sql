CREATE TABLE rol (
    id_rol BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE privilegio (
    id_privilegio BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE rol_privilegio (
    id_rol BIGINT NOT NULL,
    id_privilegio BIGINT NOT NULL,
    PRIMARY KEY (id_rol, id_privilegio),
    CONSTRAINT fk_rol_privilegio_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol),
    CONSTRAINT fk_rol_privilegio_priv FOREIGN KEY (id_privilegio) REFERENCES privilegio (id_privilegio)
);

CREATE TABLE organizacion (
    id_organizacion BIGSERIAL PRIMARY KEY,
    nombre_organizacion VARCHAR(255) NOT NULL,
    estado_organizacion VARCHAR(20) NOT NULL,
    fecha_creacion_organizacion TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_organizacion_nombre UNIQUE (nombre_organizacion),
    CONSTRAINT chk_organizacion_estado CHECK (estado_organizacion IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE local (
    id_local BIGSERIAL PRIMARY KEY,
    id_organizacion BIGINT NOT NULL,
    nombre_local VARCHAR(255) NOT NULL,
    estado_local VARCHAR(20) NOT NULL,
    fecha_creacion_local TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_local_organizacion FOREIGN KEY (id_organizacion) REFERENCES organizacion (id_organizacion),
    CONSTRAINT uq_local_org_nombre UNIQUE (id_organizacion, nombre_local),
    CONSTRAINT chk_local_estado CHECK (estado_local IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(255) NOT NULL,
    correo_usuario VARCHAR(255) NOT NULL,
    password_usuario VARCHAR(255) NOT NULL,
    estado_usuario VARCHAR(20) NOT NULL,
    fecha_creacion_usuario TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_organizacion BIGINT NOT NULL,
    CONSTRAINT uq_usuario_correo UNIQUE (correo_usuario),
    CONSTRAINT fk_usuario_organizacion FOREIGN KEY (id_organizacion) REFERENCES organizacion (id_organizacion),
    CONSTRAINT chk_usuario_estado CHECK (estado_usuario IN ('ACTIVO', 'INACTIVO', 'ELIMINADO'))
);

CREATE TABLE usuario_rol (
    id_usuario BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_correo ON usuario (correo_usuario);
CREATE INDEX idx_local_organizacion ON local (id_organizacion);
CREATE INDEX idx_usuario_organizacion ON usuario (id_organizacion);

INSERT INTO rol (nombre) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER'),
    ('ROLE_ORG_MANAGER')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO privilegio (nombre, descripcion) VALUES
    ('GESTION_USUARIOS', 'Administrar usuarios del sistema'),
    ('GESTION_ORGANIZACIONES', 'Administrar organizaciones y locales'),
    ('VER_DASHBOARD', 'Acceso al panel de control')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO rol_privilegio (id_rol, id_privilegio)
SELECT r.id_rol, p.id_privilegio
FROM rol r
JOIN privilegio p ON (
    (r.nombre = 'ROLE_ADMIN') OR
    (r.nombre = 'ROLE_ORG_MANAGER' AND p.nombre IN ('GESTION_ORGANIZACIONES', 'VER_DASHBOARD')) OR
    (r.nombre = 'ROLE_USER' AND p.nombre = 'VER_DASHBOARD')
)
ON CONFLICT DO NOTHING;

INSERT INTO organizacion (nombre_organizacion, estado_organizacion)
VALUES ('Acme S.A.', 'ACTIVO')
ON CONFLICT (nombre_organizacion) DO NOTHING;

INSERT INTO local (id_organizacion, nombre_local, estado_local)
SELECT o.id_organizacion, 'Casa Matriz', 'ACTIVO'
FROM organizacion o
WHERE o.nombre_organizacion = 'Acme S.A.'
ON CONFLICT DO NOTHING;

INSERT INTO usuario (nombre_usuario, correo_usuario, password_usuario, estado_usuario, id_organizacion)
SELECT 'Administrador General', 'admin@binexus.com', '$2b$12$DXbbJNlBvQSEGzomm/h0XebBx861x3mp/o.2tKqhRfzpIN2qqp/Pa', 'ACTIVO', o.id_organizacion
FROM organizacion o
WHERE o.nombre_organizacion = 'Acme S.A.'
ON CONFLICT (correo_usuario) DO NOTHING;

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'ROLE_ADMIN'
WHERE u.correo_usuario = 'admin@binexus.com'
ON CONFLICT DO NOTHING;
