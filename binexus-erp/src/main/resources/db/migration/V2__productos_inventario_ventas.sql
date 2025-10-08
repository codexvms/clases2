CREATE TABLE tipo_parametro (
    id_tipo_parametro SERIAL PRIMARY KEY,
    descripcion_tipo_parametro VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE parametro (
    id_parametro SERIAL PRIMARY KEY,
    id_tipo_parametro INTEGER NOT NULL REFERENCES tipo_parametro(id_tipo_parametro),
    descripcion_parametro VARCHAR(255) NOT NULL,
    estado_parametro VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT chk_parametro_estado CHECK (estado_parametro IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT uk_parametro_tipo_descripcion UNIQUE (id_tipo_parametro, descripcion_parametro)
);

CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    nombre_producto VARCHAR(255) NOT NULL,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion),
    estado_producto VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    stock_p0 NUMERIC(12,2) DEFAULT 0,
    fecha_creacion_producto TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion_producto TIMESTAMP,
    CONSTRAINT chk_producto_estado CHECK (estado_producto IN ('ACTIVO','INACTIVO'))
);

CREATE INDEX idx_producto_nombre ON producto (nombre_producto);
CREATE INDEX idx_producto_estado ON producto (estado_producto);

CREATE TABLE precio_producto (
    id_precio_producto SERIAL PRIMARY KEY,
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto) ON DELETE CASCADE,
    precio_unitario NUMERIC(12,2) NOT NULL,
    fecha_inicio_vigencia DATE NOT NULL,
    fecha_fin_vigencia DATE,
    CONSTRAINT uk_precio_producto_fecha UNIQUE (id_producto, fecha_inicio_vigencia)
);

CREATE TABLE atributo (
    id_atributo SERIAL PRIMARY KEY,
    id_producto INTEGER NOT NULL UNIQUE REFERENCES producto(id_producto) ON DELETE CASCADE,
    id_mantenedor INTEGER REFERENCES parametro(id_parametro)
);

CREATE TABLE inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto) ON DELETE CASCADE,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion),
    stock_total NUMERIC(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT uk_inventario_producto_org UNIQUE (id_producto, id_organizacion)
);

CREATE TABLE movimiento_inventario (
    id_movimiento_inventario SERIAL PRIMARY KEY,
    id_inventario INTEGER NOT NULL REFERENCES inventario(id_inventario) ON DELETE CASCADE,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion),
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad_movimiento NUMERIC(12,2) NOT NULL,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_movimiento_tipo CHECK (tipo_movimiento IN ('ENTRADA','SALIDA'))
);

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(255) NOT NULL,
    correo_cliente VARCHAR(255) NOT NULL,
    telefono_cliente VARCHAR(50),
    estado_cliente VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion),
    CONSTRAINT chk_cliente_estado CHECK (estado_cliente IN ('ACTIVO','INACTIVO')),
    CONSTRAINT uk_cliente_org_correo UNIQUE (id_organizacion, correo_cliente)
);

CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_cliente INTEGER NOT NULL REFERENCES cliente(id_cliente),
    id_local INTEGER NOT NULL REFERENCES local(id_local),
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion),
    order_id VARCHAR(40) NOT NULL UNIQUE,
    fecha_venta TIMESTAMP NOT NULL DEFAULT NOW(),
    tipo_venta VARCHAR(20) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL,
    total_venta NUMERIC(12,2) NOT NULL DEFAULT 0,
    descuento_total NUMERIC(12,2) NOT NULL DEFAULT 0,
    monto_final NUMERIC(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT chk_tipo_venta CHECK (tipo_venta IN ('PRESENCIAL','ONLINE')),
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('EFECTIVO','TARJETA','TRANSFERENCIA','OTRO')),
    CONSTRAINT chk_estado_pago CHECK (estado_pago IN ('PAGADO','PENDIENTE','RECHAZADO'))
);

CREATE INDEX idx_venta_fecha ON venta (fecha_venta DESC);
CREATE INDEX idx_venta_local ON venta (id_local);

CREATE TABLE detalle_venta (
    id_detalle_venta SERIAL PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta) ON DELETE CASCADE,
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    cantidad NUMERIC(12,2) NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL,
    total_linea NUMERIC(12,2) NOT NULL,
    costo_envio NUMERIC(12,2)
);

-- Seeds
INSERT INTO tipo_parametro (descripcion_tipo_parametro) VALUES
    ('Marca'),
    ('Tipo Producto');

INSERT INTO parametro (id_tipo_parametro, descripcion_parametro, estado_parametro) VALUES
    (1, 'Binexus Labs', 'ACTIVO'),
    (1, 'Futura Tech', 'ACTIVO'),
    (2, 'Electrónica', 'ACTIVO');

INSERT INTO producto (nombre_producto, id_organizacion, estado_producto, stock_p0)
VALUES ('Kit de inicio Binexus', 1, 'ACTIVO', 25);

INSERT INTO atributo (id_producto, id_mantenedor)
VALUES (1, 1);

INSERT INTO precio_producto (id_producto, precio_unitario, fecha_inicio_vigencia)
VALUES (1, 59990, CURRENT_DATE);

INSERT INTO inventario (id_producto, id_organizacion, stock_total)
VALUES (1, 1, 25);

INSERT INTO cliente (nombre_cliente, correo_cliente, telefono_cliente, estado_cliente, id_organizacion)
VALUES ('Cliente Demo', 'cliente.demo@binexus.cl', '+56 9 2222 3333', 'ACTIVO', 1)
ON CONFLICT DO NOTHING;
