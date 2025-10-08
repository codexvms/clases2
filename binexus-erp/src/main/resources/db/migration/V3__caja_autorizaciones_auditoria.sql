-- Auditoría base para entidades principales
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE organizacion ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE organizacion ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE local ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE local ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE producto ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE producto ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE cliente ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE inventario ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE movimiento_inventario ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE movimiento_inventario ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE parametro ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE parametro ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE tipo_parametro ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE tipo_parametro ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE precio_producto ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE precio_producto ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE atributo ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE atributo ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE privilegio ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE privilegio ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE rol ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE rol ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE venta ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE venta ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE detalle_venta ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE detalle_venta ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Cierre de caja
CREATE TABLE IF NOT EXISTS cierre_caja (
    id_cierre_caja SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha_apertura TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_cierre TIMESTAMP,
    monto_inicial NUMERIC(12,2) NOT NULL DEFAULT 0,
    monto_final NUMERIC(12,2),
    estado_caja VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT chk_estado_caja CHECK (estado_caja IN ('ABIERTO','CERRADO'))
);

CREATE INDEX IF NOT EXISTS idx_cierre_caja_usuario ON cierre_caja(id_usuario);
CREATE INDEX IF NOT EXISTS idx_cierre_caja_estado ON cierre_caja(estado_caja);

-- Autorizaciones remotas
CREATE TABLE IF NOT EXISTS autorizacion_remota (
    id_autorizacion_remota SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_cierre_caja INTEGER NOT NULL REFERENCES cierre_caja(id_cierre_caja) ON DELETE CASCADE,
    accion VARCHAR(120) NOT NULL,
    estado_autorizacion VARCHAR(20) NOT NULL,
    codigo_verificacion VARCHAR(6) NOT NULL,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT NOW(),
    autorizado_por INTEGER REFERENCES usuario(id_usuario),
    fecha_resolucion TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT chk_estado_autorizacion CHECK (estado_autorizacion IN ('PENDIENTE','APROBADA','RECHAZADA'))
);

CREATE INDEX IF NOT EXISTS idx_autorizacion_estado ON autorizacion_remota(estado_autorizacion);
CREATE UNIQUE INDEX IF NOT EXISTS uk_autorizacion_codigo ON autorizacion_remota(codigo_verificacion);
