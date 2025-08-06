SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- SCHEMA MENSAJERIA
CREATE SCHEMA IF NOT EXISTS mensajeria;
USE mensajeria;

-- 1. ESTADOS PARA PEDIDOS
CREATE TABLE estado_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 2. ESTADOS GENERALES
CREATE TABLE estado_general (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 3. ESTADOS ARQUEO
CREATE TABLE estado_arqueo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 4. ROLES DE USUARIO
CREATE TABLE rol_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    permisos JSON -- Permisos específicos del rol
);

-- 5. TIPO DE SERVICIO
CREATE TABLE tipo_servicio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    requiere_compra BOOLEAN DEFAULT FALSE
);

-- 6. TIPO DE CAMBIO PEDIDO
CREATE TABLE tipo_cambio_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 7. TIPO DE VEHICULO
CREATE TABLE tipo_vehiculo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 8. TIPO DE TURNO
CREATE TABLE tipo_turno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    hora_inicio TIME,
    hora_fin TIME
);

-- 9. TIPO DE NOTIFICACION
CREATE TABLE tipo_notificacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

-- 10. TIPO DE INGRESO ARQUEO
CREATE TABLE tipo_ingreso_arqueo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    es_automatico BOOLEAN DEFAULT FALSE -- TRUE para ingresos de pedidos, FALSE para manuales
);

-- TABLAS OPERATIVAS CON TENANT_ID
-- 11. EMPRESA MENSAJERIA
CREATE TABLE empresa_mensajeria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    email VARCHAR(100),
    estado_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (estado_id) REFERENCES estado_general(id)
);

-- 12. USUARIO
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    mensajeria_id BIGINT,
    nombre_usuario VARCHAR(50) NOT NULL,
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT NOT NULL,
    estado_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL,
    FOREIGN KEY (mensajeria_id) REFERENCES empresa_mensajeria(id) ON DELETE SET NULL,
    FOREIGN KEY (rol_id) REFERENCES rol_usuario(id),
    FOREIGN KEY (estado_id) REFERENCES estado_general(id),
    UNIQUE KEY unique_usuario_tenant (tenant_id, nombre_usuario),
    UNIQUE KEY unique_email_tenant (tenant_id, email)
);

-- 13. DIRECCION
CREATE TABLE direccion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    barrio VARCHAR(100),
    direccion_completa VARCHAR(255) NOT NULL,
    es_recogida BOOLEAN DEFAULT FALSE,
    es_entrega BOOLEAN DEFAULT FALSE,
    estado_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (estado_id) REFERENCES estado_general(id)
);

-- 14. CLIENTE
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    mensajeria_id BIGINT NOT NULL,
    nombre VARCHAR(100),
    telefono VARCHAR(15),
    frecuencia_pedidos INT DEFAULT 0,
    ultimo_pedido TIMESTAMP NULL,
    descuento_porcentaje DECIMAL(5,2) DEFAULT 0.00,
    estado_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mensajeria_id) REFERENCES empresa_mensajeria(id) ON DELETE CASCADE,
    FOREIGN KEY (estado_id) REFERENCES estado_general(id)
);

-- 15. CLIENTE_DIRECCION 
CREATE TABLE cliente_direccion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    direccion_id BIGINT NOT NULL,
    es_predeterminada_recogida BOOLEAN DEFAULT FALSE,
    es_predeterminada_entrega BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (direccion_id) REFERENCES direccion(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cliente_direccion (cliente_id, direccion_id)
);

-- 16. TARIFA 
CREATE TABLE tarifa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    mensajeria_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    valor_fijo DECIMAL(10,2) NOT NULL, 
    descripcion VARCHAR(200),
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mensajeria_id) REFERENCES empresa_mensajeria(id)
);

-- 17. MENSAJERO 
CREATE TABLE mensajero (
    id BIGINT PRIMARY KEY, 
    tenant_id BIGINT NOT NULL,
    disponibilidad BOOLEAN DEFAULT TRUE,
    pedidos_activos INT DEFAULT 0, 
    max_pedidos_simultaneos INT DEFAULT 5,
    tipo_vehiculo_id INT NOT NULL DEFAULT 1,
    total_entregas INT DEFAULT 0,
    fecha_ultima_entrega TIMESTAMP NULL,
    estado_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (tipo_vehiculo_id) REFERENCES tipo_vehiculo(id),
    FOREIGN KEY (estado_id) REFERENCES estado_general(id)
);

-- 18. PEDIDO 
CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT,
    mensajeria_id BIGINT NOT NULL,
    mensajero_id BIGINT,
    tipo_servicio_id INT NOT NULL,
    tarifa_id BIGINT,
    -- Referencias a direcciones o datos directos para pedidos sin cliente
    direccion_recogida_id BIGINT,
    direccion_entrega_id BIGINT,
    -- Campos para pedidos sin cliente registrado
    direccion_recogida_temporal VARCHAR(255),
    direccion_entrega_temporal VARCHAR(255),
    ciudad_recogida VARCHAR(100),
    barrio_recogida VARCHAR(100),
    ciudad_entrega VARCHAR(100),
    barrio_entrega VARCHAR(100),
    
    telefono_recogida VARCHAR(20) NOT NULL,
    telefono_entrega VARCHAR(20) NOT NULL,
    tipo_paquete VARCHAR(100),
    peso_kg DECIMAL(6,2),
    valor_declarado DECIMAL(10,2) DEFAULT 0.00,
    costo_compra DECIMAL(10,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2),
    total DECIMAL(10,2),
    estado_id INT NOT NULL DEFAULT 1,
    tiempo_entrega_minutos INT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega TIMESTAMP NULL,
    notas TEXT,
    
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (mensajeria_id) REFERENCES empresa_mensajeria(id),
    FOREIGN KEY (mensajero_id) REFERENCES usuario(id),
    FOREIGN KEY (tipo_servicio_id) REFERENCES tipo_servicio(id),
    FOREIGN KEY (estado_id) REFERENCES estado_pedido(id),
    FOREIGN KEY (tarifa_id) REFERENCES tarifa(id),
    FOREIGN KEY (direccion_recogida_id) REFERENCES direccion(id),
    FOREIGN KEY (direccion_entrega_id) REFERENCES direccion(id)
);

-- 19. HISTORIAL DE PEDIDO
CREATE TABLE historial_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    tipo_cambio_id INT NOT NULL,
    valor_anterior TEXT,
    valor_nuevo TEXT,
    usuario_id BIGINT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (tipo_cambio_id) REFERENCES tipo_cambio_pedido(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL
);

-- 20. ARQUEO DE CAJA 
CREATE TABLE arqueo_caja (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    mensajeria_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    turno_id INT NOT NULL,
    
    efectivo_inicio DECIMAL(10,2) DEFAULT 0.00,
    total_ingresos DECIMAL(10,2) DEFAULT 0.00,
    egresos DECIMAL(10,2) DEFAULT 0.00,
    efectivo_real DECIMAL(10,2) DEFAULT 0.00,
    diferencia DECIMAL(10,2) GENERATED ALWAYS AS (efectivo_real - (efectivo_inicio + total_ingresos - egresos)) STORED,

    estado_id INT NOT NULL DEFAULT 1,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (mensajeria_id) REFERENCES empresa_mensajeria(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (turno_id) REFERENCES tipo_turno(id),
    FOREIGN KEY (estado_id) REFERENCES estado_arqueo(id),

    UNIQUE KEY unique_arqueo_turno (tenant_id, mensajeria_id, fecha, turno_id)
);

-- 21. INGRESO ARQUEO 
CREATE TABLE ingreso_arqueo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    arqueo_id BIGINT NOT NULL,
    tipo_ingreso_id INT NOT NULL,
    pedido_id BIGINT NULL, -- Solo para ingresos automáticos de pedidos
    monto DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (arqueo_id) REFERENCES arqueo_caja(id) ON DELETE CASCADE,
    FOREIGN KEY (tipo_ingreso_id) REFERENCES tipo_ingreso_arqueo(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);

-- 22. NOTIFICACIONES
CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    usuario_id BIGINT,
    tipo_notificacion_id INT NOT NULL,
    titulo VARCHAR(150),
    mensaje TEXT,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (tipo_notificacion_id) REFERENCES tipo_notificacion(id)
);

-- 1. AGREGAR ÍNDICES PARA OPTIMIZACIÓN
-- Índices para tenant_id 
CREATE INDEX idx_empresa_mensajeria_tenant ON empresa_mensajeria(tenant_id);
CREATE INDEX idx_usuario_tenant ON usuario(tenant_id);
CREATE INDEX idx_cliente_tenant ON cliente(tenant_id);
CREATE INDEX idx_tarifa_tenant ON tarifa(tenant_id);
CREATE INDEX idx_mensajero_tenant ON mensajero(tenant_id);
CREATE INDEX idx_pedido_tenant ON pedido(tenant_id);
CREATE INDEX idx_arqueo_caja_tenant ON arqueo_caja(tenant_id);
CREATE INDEX idx_notificacion_tenant ON notificacion(tenant_id);

-- Índices para consultas frecuentes
CREATE INDEX idx_pedido_estado_fecha ON pedido(estado_id, fecha_creacion);
CREATE INDEX idx_pedido_mensajero_estado ON pedido(mensajero_id, estado_id);
CREATE INDEX idx_pedido_cliente ON pedido(cliente_id);
CREATE INDEX idx_pedido_fecha ON pedido(fecha_creacion);
CREATE INDEX idx_pedido_estado ON pedido(estado_id);
CREATE INDEX idx_direccion_ciudad ON direccion(ciudad);
CREATE INDEX idx_cliente_direccion_cliente ON cliente_direccion(cliente_id);
CREATE INDEX idx_ingreso_arqueo_pedido ON ingreso_arqueo(pedido_id);
CREATE INDEX idx_ingreso_arqueo_fecha ON ingreso_arqueo(fecha_creacion);
CREATE INDEX idx_cliente_ultimo_pedido ON cliente(ultimo_pedido);
CREATE INDEX idx_historial_pedido_fecha ON historial_pedido(fecha);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_mensajero_disponibilidad ON mensajero(disponibilidad, estado_id);

-- 1. TRIGGERS PARA GESTIÓN DE MENSAJEROS
-- Trigger: Crear mensajero automáticamente cuando se crea un usuario con rol mensajero
DELIMITER $$
CREATE TRIGGER trg_crear_mensajero_usuario
AFTER INSERT ON usuario
FOR EACH ROW
BEGIN
    DECLARE v_rol_mensajero VARCHAR(50);
    
    -- Obtener nombre del rol
    SELECT nombre INTO v_rol_mensajero
    FROM rol_usuario 
    WHERE id = NEW.rol_id;
    
    -- Si es mensajero, crear registro en tabla mensajero
    IF v_rol_mensajero = 'mensajero' OR v_rol_mensajero = 'MENSAJERO' THEN
        INSERT INTO mensajero (
            id, 
            tenant_id, 
            disponibilidad, 
            pedidos_activos, 
            max_pedidos_simultaneos,
            tipo_vehiculo_id,
            total_entregas,
            estado_id
        ) VALUES (
            NEW.id,
            NEW.tenant_id,
            TRUE,
            0,
            5,
            1, 
            0,
            NEW.estado_id
        );
    END IF;
END$$
DELIMITER ;

-- Trigger: Actualizar o eliminar mensajero cuando se cambia rol de usuario
DELIMITER $$
CREATE TRIGGER trg_actualizar_mensajero_usuario
AFTER UPDATE ON usuario
FOR EACH ROW
BEGIN
    DECLARE v_rol_anterior VARCHAR(50);
    DECLARE v_rol_nuevo VARCHAR(50);
    DECLARE v_existe_mensajero INT DEFAULT 0;
    
    -- Obtener nombres de roles
    SELECT nombre INTO v_rol_anterior FROM rol_usuario WHERE id = OLD.rol_id;
    SELECT nombre INTO v_rol_nuevo FROM rol_usuario WHERE id = NEW.rol_id;
    
    -- Verificar si existe como mensajero
    SELECT COUNT(*) INTO v_existe_mensajero FROM mensajero WHERE id = NEW.id;
    
    -- Si cambió de mensajero a otro rol, eliminar mensajero
    IF (v_rol_anterior IN ('mensajero', 'MENSAJERO')) AND 
       (v_rol_nuevo NOT IN ('mensajero', 'MENSAJERO')) AND 
       v_existe_mensajero > 0 THEN
        DELETE FROM mensajero WHERE id = NEW.id;
    END IF;
    
    -- Si cambió de otro rol a mensajero, crear mensajero
    IF (v_rol_anterior NOT IN ('mensajero', 'MENSAJERO')) AND 
       (v_rol_nuevo IN ('mensajero', 'MENSAJERO')) AND 
       v_existe_mensajero = 0 THEN
        INSERT INTO mensajero (
            id, tenant_id, disponibilidad, pedidos_activos, 
            max_pedidos_simultaneos, tipo_vehiculo_id, total_entregas, estado_id
        ) VALUES (
            NEW.id, NEW.tenant_id, TRUE, 0, 5, 1, 0, NEW.estado_id
        );
    END IF;
    
    -- Si sigue siendo mensajero, actualizar estado
    IF (v_rol_nuevo IN ('mensajero', 'MENSAJERO')) AND v_existe_mensajero > 0 THEN
        UPDATE mensajero 
        SET estado_id = NEW.estado_id
        WHERE id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- Trigger: Eliminar mensajero cuando se elimina usuario mensajero
DELIMITER $$
CREATE TRIGGER trg_eliminar_mensajero_usuario
BEFORE DELETE ON usuario
FOR EACH ROW
BEGIN
    DECLARE v_rol_usuario VARCHAR(50);
    
    SELECT nombre INTO v_rol_usuario FROM rol_usuario WHERE id = OLD.rol_id;
    
    IF v_rol_usuario IN ('mensajero', 'MENSAJERO') THEN
        DELETE FROM mensajero WHERE id = OLD.id;
    END IF;
END$$
DELIMITER ;

-- 2. TRIGGERS PARA GESTIÓN DE PEDIDOS Y MENSAJEROS
-- Trigger: Actualizar contador de pedidos activos del mensajero
DELIMITER $$
CREATE TRIGGER trg_actualizar_pedidos_mensajero_insert
AFTER INSERT ON pedido
FOR EACH ROW
BEGIN
    IF NEW.mensajero_id IS NOT NULL THEN
        UPDATE mensajero 
        SET pedidos_activos = pedidos_activos + 1,
            disponibilidad = CASE 
                WHEN (pedidos_activos + 1) > max_pedidos_simultaneos THEN FALSE 
                ELSE TRUE 
            END
        WHERE id = NEW.mensajero_id;
    END IF;
END$$
DELIMITER ;

-- Trigger: Actualizar pedidos activos cuando se cambia estado o mensajero
DELIMITER $$
CREATE TRIGGER trg_actualizar_pedidos_mensajero_update
AFTER UPDATE ON pedido
FOR EACH ROW
BEGIN
    DECLARE v_estado_entregado INT;
    DECLARE v_estado_cancelado INT;
    
    -- Obtener IDs de estados finales
    SELECT id INTO v_estado_entregado FROM estado_pedido WHERE nombre = 'entregado' LIMIT 1;
    SELECT id INTO v_estado_cancelado FROM estado_pedido WHERE nombre = 'cancelado' LIMIT 1;
    
    -- Si cambió de mensajero
    IF OLD.mensajero_id != NEW.mensajero_id OR (OLD.mensajero_id IS NULL AND NEW.mensajero_id IS NOT NULL) OR (OLD.mensajero_id IS NOT NULL AND NEW.mensajero_id IS NULL) THEN
        
        -- Decrementar pedidos del mensajero anterior
        IF OLD.mensajero_id IS NOT NULL THEN
            UPDATE mensajero 
            SET pedidos_activos = GREATEST(pedidos_activos - 1, 0),
                disponibilidad = CASE 
                    WHEN (pedidos_activos - 1) < max_pedidos_simultaneos THEN TRUE 
                    ELSE disponibilidad 
                END
            WHERE id = OLD.mensajero_id;
        END IF;
        
        -- Incrementar pedidos del nuevo mensajero
        IF NEW.mensajero_id IS NOT NULL THEN
            UPDATE mensajero 
            SET pedidos_activos = pedidos_activos + 1,
                disponibilidad = CASE 
                    WHEN (pedidos_activos + 1) > max_pedidos_simultaneos THEN FALSE 
                    ELSE TRUE 
                END
            WHERE id = NEW.mensajero_id;
        END IF;
    END IF;
    
    -- Si el pedido se completó o canceló
    IF (NEW.estado_id IN (v_estado_entregado, v_estado_cancelado)) AND 
       (OLD.estado_id NOT IN (v_estado_entregado, v_estado_cancelado)) AND 
       NEW.mensajero_id IS NOT NULL THEN
        
        UPDATE mensajero 
        SET pedidos_activos = GREATEST(pedidos_activos - 1, 0),
            total_entregas = CASE WHEN NEW.estado_id = v_estado_entregado THEN total_entregas + 1 ELSE total_entregas END,
            fecha_ultima_entrega = CASE WHEN NEW.estado_id = v_estado_entregado THEN NOW() ELSE fecha_ultima_entrega END,
            disponibilidad = CASE 
                WHEN (pedidos_activos - 1) < max_pedidos_simultaneos THEN TRUE 
                ELSE disponibilidad 
            END
        WHERE id = NEW.mensajero_id;
    END IF;
END$$
DELIMITER ;

-- Trigger para validar sincronizacion en INSERT/UPDATE de mensajero
DELIMITER $$
CREATE TRIGGER trg_sync_estado_usuario
AFTER UPDATE ON usuario
FOR EACH ROW
BEGIN
    DECLARE v_estado_mensajero INT;

    -- Verificar si hay un mensajero relacionado
    SELECT estado_id INTO v_estado_mensajero
    FROM mensajero
    WHERE id = NEW.id;

    -- Si existe y el estado es diferente, actualizar
    IF v_estado_mensajero IS NOT NULL AND v_estado_mensajero != NEW.estado_id THEN
        UPDATE mensajero
        SET estado_id = NEW.estado_id
        WHERE id = NEW.id;
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_sync_estado_mensajero
AFTER UPDATE ON mensajero
FOR EACH ROW
BEGIN
    DECLARE v_estado_usuario INT;

    -- Verificar si hay un usuario relacionado
    SELECT estado_id INTO v_estado_usuario
    FROM usuario
    WHERE id = NEW.id;

    -- Si existe y el estado es diferente, actualizar
    IF v_estado_usuario IS NOT NULL AND v_estado_usuario != NEW.estado_id THEN
        UPDATE usuario
        SET estado_id = NEW.estado_id
        WHERE id = NEW.id;
    END IF;
END$$
DELIMITER ;


-- 3. TRIGGERS PARA CÁLCULO DE TOTALES Y FRECUENCIA DE CLIENTES
-- Trigger: Calcular total del pedido
DELIMITER $$

CREATE TRIGGER trg_calcular_total_pedido
BEFORE INSERT ON pedido
FOR EACH ROW
BEGIN
    DECLARE v_valor_tarifa DECIMAL(10,2) DEFAULT 5000; -- Valor por defecto si no se encuentra tarifa
    DECLARE v_descuento DECIMAL(10,2) DEFAULT 0;
    DECLARE v_subtotal DECIMAL(10,2);

    -- Obtener tarifa específica o tarifa base
    IF NEW.tarifa_id IS NOT NULL THEN
        SELECT valor_fijo INTO v_valor_tarifa
        FROM tarifa 
        WHERE id = NEW.tarifa_id AND activa = TRUE
        LIMIT 1;
    ELSE
        SELECT valor_fijo INTO v_valor_tarifa
        FROM tarifa 
        WHERE mensajeria_id = NEW.mensajeria_id AND activa = TRUE
        ORDER BY fecha_creacion DESC 
        LIMIT 1;
    END IF;

    -- Obtener descuento del cliente si existe
    IF NEW.cliente_id IS NOT NULL THEN
        SELECT COALESCE(descuento_porcentaje, 0) INTO v_descuento
        FROM cliente 
        WHERE id = NEW.cliente_id;
    END IF;

    -- Establecer valores por defecto si son NULL
    SET NEW.costo_compra = COALESCE(NEW.costo_compra, 0);
    SET NEW.valor_declarado = COALESCE(NEW.valor_declarado, 0);

    -- Calcular subtotal
    SET v_subtotal = v_valor_tarifa + NEW.costo_compra;
    SET NEW.subtotal = v_subtotal;

    -- Calcular total con descuento aplicado solo al subtotal
    SET NEW.total = v_subtotal - (v_subtotal * v_descuento / 100);
END$$

DELIMITER ;

-- Trigger: Actualizar total cuando se modifica el pedido
DELIMITER $$
CREATE TRIGGER trg_actualizar_total_pedido
BEFORE UPDATE ON pedido
FOR EACH ROW
BEGIN
    DECLARE v_valor_tarifa DECIMAL(10,2) DEFAULT 5000;
    DECLARE v_descuento DECIMAL(10,2) DEFAULT 0;
    DECLARE v_subtotal DECIMAL(10,2);

    -- Obtener tarifa específica o tarifa base
    IF NEW.tarifa_id IS NOT NULL THEN
        SELECT valor_fijo INTO v_valor_tarifa
        FROM tarifa 
        WHERE id = NEW.tarifa_id AND activa = TRUE
        LIMIT 1;
    ELSE
        SELECT valor_fijo INTO v_valor_tarifa
        FROM tarifa 
        WHERE mensajeria_id = NEW.mensajeria_id AND activa = TRUE
        ORDER BY fecha_creacion DESC 
        LIMIT 1;
    END IF;

    -- Obtener descuento del cliente si existe
    IF NEW.cliente_id IS NOT NULL THEN
        SELECT COALESCE(descuento_porcentaje, 0) INTO v_descuento
        FROM cliente 
        WHERE id = NEW.cliente_id;
    END IF;

    -- Establecer valores por defecto si son NULL
    SET NEW.costo_compra = COALESCE(NEW.costo_compra, 0);
    SET NEW.valor_declarado = COALESCE(NEW.valor_declarado, 0);

    -- Calcular subtotal
    SET v_subtotal = v_valor_tarifa + NEW.costo_compra;
    SET NEW.subtotal = v_subtotal;

    -- Calcular total con descuento aplicado solo al subtotal
    SET NEW.total = v_subtotal - (v_subtotal * v_descuento / 100);
END$$

DELIMITER ;


-- Trigger: Actualizar frecuencia de pedidos del cliente
DELIMITER $$
CREATE TRIGGER trg_actualizar_frecuencia_cliente
AFTER INSERT ON pedido
FOR EACH ROW
BEGIN
    IF NEW.cliente_id IS NOT NULL THEN
        UPDATE cliente 
        SET frecuencia_pedidos = frecuencia_pedidos + 1,
            ultimo_pedido = NOW()
        WHERE id = NEW.cliente_id;
    END IF;
END$$
DELIMITER ;

-- 4. TRIGGERS PARA HISTORIAL DE PEDIDOS
-- Trigger: Registrar cambios en historial de pedidos
DELIMITER $$
CREATE TRIGGER trg_historial_pedido_update
AFTER UPDATE ON pedido
FOR EACH ROW
BEGIN
    -- 1. Cambio de estado
    IF OLD.estado_id != NEW.estado_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 1, OLD.estado_id, NEW.estado_id, @usuario_actual);
    END IF;
    
    -- 2. Cambio de cliente
    IF OLD.cliente_id != NEW.cliente_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 2, OLD.cliente_id, NEW.cliente_id, @usuario_actual);
    END IF;
    
    -- 3. Cambio de mensajero
    IF COALESCE(OLD.mensajero_id, 0) != COALESCE(NEW.mensajero_id, 0) THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 3, COALESCE(OLD.mensajero_id, 'NULL'), COALESCE(NEW.mensajero_id, 'NULL'), @usuario_actual);
    END IF;
    
    -- 4. Cambio de dirección recogida
    IF OLD.direccion_recogida_id != NEW.direccion_recogida_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 4, OLD.direccion_recogida_id, NEW.direccion_recogida_id, @usuario_actual);
    END IF;
    
    -- 5. Cambio de dirección entrega
    IF OLD.direccion_entrega_id != NEW.direccion_entrega_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 5, OLD.direccion_entrega_id, NEW.direccion_entrega_id, @usuario_actual);
    END IF;
    
    -- 6. Cambio de teléfono recogida
    IF COALESCE(OLD.telefono_recogida, '') != COALESCE(NEW.telefono_recogida, '') THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 6, OLD.telefono_recogida, NEW.telefono_recogida, @usuario_actual);
    END IF;
    
    -- 7. Cambio de teléfono entrega
    IF COALESCE(OLD.telefono_entrega, '') != COALESCE(NEW.telefono_entrega, '') THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 7, OLD.telefono_entrega, NEW.telefono_entrega, @usuario_actual);
    END IF;
    
    -- 8. Cambio de notas
    IF COALESCE(OLD.notas, '') != COALESCE(NEW.notas, '') THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 8, OLD.notas, NEW.notas, @usuario_actual);
    END IF;
    
    -- 9. Cambio de tipo de servicio
    IF OLD.tipo_servicio_id != NEW.tipo_servicio_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 9, OLD.tipo_servicio_id, NEW.tipo_servicio_id, @usuario_actual);
    END IF;
    
    -- 10. Cambio de tipo de paquete
    IF COALESCE(OLD.tipo_paquete, '') != COALESCE(NEW.tipo_paquete, '') THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 10, OLD.tipo_paquete, NEW.tipo_paquete, @usuario_actual);
    END IF;
    
    -- 11. Cambio de tarifa
    IF OLD.tarifa_id != NEW.tarifa_id THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 11, OLD.tarifa_id, NEW.tarifa_id, @usuario_actual);
    END IF;
    
    -- 12. Cambio en el total
    IF OLD.total != NEW.total THEN
        INSERT INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id)
        VALUES (NEW.id, 12, OLD.total, NEW.total, @usuario_actual);
    END IF;
END$$
DELIMITER ;

-- Trigger arqueo de caja automatico
DELIMITER $$

CREATE TRIGGER trg_ingreso_arqueo_insert
AFTER INSERT ON ingreso_arqueo
FOR EACH ROW
BEGIN
  UPDATE arqueo_caja
  SET total_ingresos = (
    SELECT COALESCE(SUM(monto), 0)
    FROM ingreso_arqueo
    WHERE arqueo_id = NEW.arqueo_id
  )
  WHERE id = NEW.arqueo_id;
END$$

DELIMITER ;

-- Trigger para update en el arqueo de caja
DELIMITER $$

CREATE TRIGGER trg_ingreso_arqueo_update
AFTER UPDATE ON ingreso_arqueo
FOR EACH ROW
BEGIN
  -- Actualiza el arqueo anterior (si cambió)
  IF OLD.arqueo_id != NEW.arqueo_id THEN
    UPDATE arqueo_caja
    SET total_ingresos = (
      SELECT COALESCE(SUM(monto), 0)
      FROM ingreso_arqueo
      WHERE arqueo_id = OLD.arqueo_id
    )
    WHERE id = OLD.arqueo_id;
  END IF;

  -- Actualiza el nuevo arqueo
  UPDATE arqueo_caja
  SET total_ingresos = (
    SELECT COALESCE(SUM(monto), 0)
    FROM ingreso_arqueo
    WHERE arqueo_id = NEW.arqueo_id
  )
  WHERE id = NEW.arqueo_id;
END$$

DELIMITER ;

-- Trigger para la elimincación de algun ingreso
DELIMITER $$

CREATE TRIGGER trg_ingreso_arqueo_delete
AFTER DELETE ON ingreso_arqueo
FOR EACH ROW
BEGIN
  UPDATE arqueo_caja
  SET total_ingresos = (
    SELECT COALESCE(SUM(monto), 0)
    FROM ingreso_arqueo
    WHERE arqueo_id = OLD.arqueo_id
  )
  WHERE id = OLD.arqueo_id;
END$$

DELIMITER ;

-- Trigger para ingreso automatizado pedido entregado
DELIMITER $$
CREATE TRIGGER trg_pedido_entregado_arqueo
AFTER UPDATE ON pedido
FOR EACH ROW
BEGIN
  DECLARE arqueo_activo_id INT DEFAULT NULL;
  DECLARE ingreso_existe INT DEFAULT 0;
  
  -- Solo ejecutar si el estado cambió a "entregado"
  IF OLD.estado_id != NEW.estado_id AND NEW.estado_id = 4 THEN
    
    -- Verificar si ya existe un ingreso para este pedido (evitar duplicados)
    SELECT COUNT(*) INTO ingreso_existe
    FROM ingreso_arqueo
    WHERE pedido_id = NEW.id;
    
    -- Solo proceder si no existe ya un ingreso para este pedido
    IF ingreso_existe = 0 THEN
      
      -- Buscar arqueo abierto
      SELECT id INTO arqueo_activo_id
      FROM arqueo_caja
      WHERE fecha = DATE(NEW.fecha_entrega)
        AND estado_id = 1 -- Estado "abierto"
        AND tenant_id = NEW.tenant_id
        AND mensajeria_id = NEW.mensajeria_id
      ORDER BY fecha_creacion DESC
      LIMIT 1;
      
      -- Registrar ingreso si hay arqueo abierto
      IF arqueo_activo_id IS NOT NULL THEN
        INSERT INTO ingreso_arqueo (
          arqueo_id, 
          tipo_ingreso_id, 
          pedido_id, 
          monto, 
          descripcion, 
          fecha_creacion
        )
        VALUES (
          arqueo_activo_id,
          1, -- PEDIDO_ENTREGADO
          NEW.id,
          NEW.total,
          CONCAT('Ingreso automático por entrega pedido #', NEW.id),
          NOW()
        );
      END IF;
      
    END IF;
    
  END IF;
END$$
DELIMITER ;

-- 5. VISTAS PARA ESTADÍSTICAS DE MENSAJEROS
-- Vista: Estadísticas generales de mensajeros
CREATE VIEW v_estadisticas_mensajeros AS
SELECT 
    m.id,
    u.nombres,
    u.apellidos,
    u.tenant_id,
    u.mensajeria_id,
    m.total_entregas,
    m.pedidos_activos,
    m.disponibilidad,
    tv.nombre as tipo_vehiculo,
    
    -- Tiempo promedio de entrega (en minutos)
    COALESCE(AVG(p.tiempo_entrega_minutos), 0) as tiempo_promedio_entrega,
    
    -- Promedio de pedidos por día (últimos 30 días)
    COALESCE(COUNT(CASE WHEN p.fecha_creacion >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) / 30.0, 0) as promedio_pedidos_dia,
    
    -- Pedidos completados últimos 30 días
    COUNT(CASE WHEN p.fecha_entrega >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND ep.nombre = 'entregado' THEN 1 END) as pedidos_mes_actual,
    
    -- Tasa de éxito (pedidos entregados vs asignados)
    CASE 
        WHEN COUNT(p.id) > 0 THEN 
            (COUNT(CASE WHEN ep.nombre = 'entregado' THEN 1 END) * 100.0 / COUNT(p.id))
        ELSE 0 
    END as tasa_exito_porcentaje,
    
    
    m.fecha_ultima_entrega,
    
    -- Ingresos generados (suma de totales de pedidos entregados)
    COALESCE(SUM(CASE WHEN ep.nombre = 'entregado' THEN p.total ELSE 0 END), 0) as ingresos_generados

FROM mensajero m
INNER JOIN usuario u ON m.id = u.id
LEFT JOIN tipo_vehiculo tv ON m.tipo_vehiculo_id = tv.id
LEFT JOIN pedido p ON m.id = p.mensajero_id
LEFT JOIN estado_pedido ep ON p.estado_id = ep.id
GROUP BY m.id, u.nombres, u.apellidos, u.tenant_id, u.mensajeria_id, 
         m.total_entregas, m.pedidos_activos, m.disponibilidad, tv.nombre, m.fecha_ultima_entrega;

-- Vista: Ranking de mensajeros por desempeño
CREATE VIEW v_ranking_mensajeros AS
SELECT 
    em.*,
    RANK() OVER (PARTITION BY em.tenant_id, em.mensajeria_id ORDER BY em.tasa_exito_porcentaje DESC, em.total_entregas DESC) as ranking_desempeno,
    CASE 
        WHEN em.tasa_exito_porcentaje >= 95 AND em.total_entregas >= 50 THEN 'EXCELENTE'
        WHEN em.tasa_exito_porcentaje >= 85 AND em.total_entregas >= 20 THEN 'BUENO'
        WHEN em.tasa_exito_porcentaje >= 70 THEN 'REGULAR'
        ELSE 'NECESITA MEJORA'
    END as categoria_desempeno
FROM v_estadisticas_mensajeros em
WHERE em.total_entregas > 0;

-- Vista: Resumen de arqueos por mensajería
CREATE VIEW v_resumen_arqueos AS
SELECT 
    ac.tenant_id,
    ac.mensajeria_id,
    em.nombre as empresa,
    DATE_FORMAT(ac.fecha, '%Y-%m') as mes_ano,
    tt.nombre as turno,
    COUNT(*) as total_arqueos,
    SUM(CASE WHEN ABS(ac.diferencia) <= 1000 THEN 1 ELSE 0 END) as arqueos_ok,
    SUM(CASE WHEN ABS(ac.diferencia) > 1000 THEN 1 ELSE 0 END) as arqueos_con_diferencia,
    AVG(ac.diferencia) as diferencia_promedio,
    SUM(ac.total_ingresos) as total_ingresos_mes,
    SUM(ac.egresos) as total_egresos_mes,
    MAX(ABS(ac.diferencia)) as mayor_diferencia
FROM arqueo_caja ac
INNER JOIN empresa_mensajeria em ON ac.mensajeria_id = em.id
INNER JOIN tipo_turno tt ON ac.turno_id = tt.id
WHERE ac.fecha >= DATE_SUB(CURRENT_DATE, INTERVAL 6 MONTH)
GROUP BY ac.tenant_id, ac.mensajeria_id, em.nombre, DATE_FORMAT(ac.fecha, '%Y-%m'), tt.nombre
ORDER BY ac.tenant_id, ac.mensajeria_id, mes_ano DESC, tt.nombre;

-- Dashboard general
CREATE VIEW v_dashboard_general AS
SELECT 
    p.tenant_id,
    p.mensajeria_id,
    em.nombre as empresa,
    DATE(p.fecha_creacion) as fecha,
    
    -- Contadores generales
    COUNT(*) as total_pedidos,
    COUNT(CASE WHEN ep.nombre = 'entregado' THEN 1 END) as entregados,
    COUNT(CASE WHEN ep.nombre = 'cancelado' THEN 1 END) as cancelados,
    COUNT(CASE WHEN ep.nombre IN ('pendiente', 'asignado', 'en_transito') THEN 1 END) as activos,
    
    -- Financiero
    SUM(CASE WHEN ep.nombre = 'entregado' THEN p.total ELSE 0 END) as ingresos,
    AVG(CASE WHEN ep.nombre = 'entregado' THEN p.total END) as ticket_promedio,
    
    -- Operacional
    COUNT(DISTINCT p.mensajero_id) as mensajeros_activos,
    AVG(CASE WHEN ep.nombre = 'entregado' AND p.tiempo_entrega_minutos IS NOT NULL THEN p.tiempo_entrega_minutos END) as tiempo_promedio,
    
    -- Eficiencia
    CASE 
        WHEN COUNT(*) > 0 THEN ROUND((COUNT(CASE WHEN ep.nombre = 'entregado' THEN 1 END) * 100.0 / COUNT(*)), 2)
        ELSE 0 
    END as tasa_exito

FROM pedido p
INNER JOIN empresa_mensajeria em ON p.mensajeria_id = em.id
LEFT JOIN estado_pedido ep ON p.estado_id = ep.id
WHERE p.fecha_creacion >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
GROUP BY p.tenant_id, p.mensajeria_id, em.nombre, DATE(p.fecha_creacion)
ORDER BY p.tenant_id, p.mensajeria_id, fecha DESC;

-- 6. DATOS INICIALES REQUERIDOS
-- Insertar estados básicos 
INSERT IGNORE INTO estado_pedido (nombre, descripcion) VALUES
('pendiente', 'Pedido creado, esperando asignación'),
('asignado', 'Pedido asignado a mensajero'),
('en_transito', 'Mensajero en camino'),
('entregado', 'Pedido entregado exitosamente'),
('cancelado', 'Pedido cancelado');

-- Insertar estados generales
INSERT IGNORE INTO estado_general (nombre, descripcion) VALUES
('activo', 'Registro activo'),
('inactivo', 'Registro inactivo'),
('suspendido', 'Registro suspendido temporalmente');

-- Insertar estados arqueo
INSERT IGNORE INTO estado_arqueo (nombre, descripcion) VALUES
('abierto', 'Arqueo en proceso'),
('cerrado', 'Arqueo finalizado'),
('con_diferencia', 'Arqueo con diferencias detectadas');

-- Insertar tipos de cambio del pedido
INSERT IGNORE INTO tipo_cambio_pedido (nombre, descripcion) VALUES
('cambio_estado', 'Cambio de estado del pedido'),
('cambio_cliente', 'Cambio del cliente'),
('cambio_mensajero', 'Cambio de mensajero asignado'),
('cambio_direccion_recogida', 'Cambio de dirección recogida'),
('cambio_direccion_entrega', 'Cambio de dirección entrega'),
('cambio_telefono_recogida', 'Cambio de teléfono recogida'),
('cambio_telefono_entrega', 'Cambio de teléfono entrega'),
('cambio_notas', 'Cambio de notas del pedido'),
('cambio_tipo_servicio', 'Cambio del tipo de servicio'),
('cambio_tipo_paquete', 'Cambio del tipo de paquete'),
('cambio_tarifa', 'Cambio de tarifa del pedido'),
('cambio_total', 'Cambio en el total del pedido');

-- Insertar tipos de notificaciones
INSERT IGNORE INTO tipo_notificacion (nombre, descripcion) VALUES
('alerta_arqueo', 'Alerta de diferencias en arqueo'),
('pedido_asignado', 'Notificación de pedido asignado'),
('pedido_cambio', 'Notificación de cambio en el pedido'),
('pedido_completado', 'Notificación de pedido completado');

-- Insertar roles
INSERT IGNORE INTO rol_usuario (nombre, descripcion, permisos) VALUES
('SUPER_ADMIN', 'Administrador del sistema', '{"all": true}'),
('ADMIN_MENSAJERIA', 'Administrador del sistema', '{"all": true}'),
('MENSAJERO', 'Mensajero de la empresa', '{"pedidos": ["read", "update_estado"]}'),
('OPERADOR', 'Operador de pedidos', '{"pedidos": ["create", "read", "update", "assign"]}');

-- Insertar tipo de vehiculos
INSERT IGNORE INTO tipo_vehiculo (nombre, descripcion) VALUES
('moto', 'Motocicleta'),
('bicicleta', 'Bicicleta'),
('carro', 'Automóvil'),
('a_pie', 'A pie');

-- Insertar tipo de turnos
INSERT IGNORE INTO tipo_turno (nombre, hora_inicio, hora_fin) VALUES
('mañana', '06:00:00', '14:00:00'),
('tarde', '14:00:00', '22:00:00'),
('noche', '22:00:00', '06:00:00');

-- Insertar tipo de servicios
INSERT IGNORE INTO tipo_servicio (nombre, descripcion, requiere_compra) VALUES
('express', 'Entrega rápida en menos de 2 horas', FALSE),
('normal', 'Entrega estándar en el día', FALSE),
('programado', 'Entrega programada para fecha específica', FALSE),
('compra_express', 'Compra y entrega rápida', TRUE),
('compra_normal', 'Compra y entrega estándar', TRUE),
('documentos', 'Envío de documentos importantes', FALSE),
('medicamentos', 'Entrega de medicamentos', FALSE),
('alimentos', 'Entrega de comida y bebidas', FALSE),
('regalos', 'Entrega de regalos y flores', FALSE),
('electrodomesticos', 'Entrega de electrodomésticos grandes', FALSE);

-- Insertar tipo arqueo
INSERT IGNORE INTO tipo_ingreso_arqueo (nombre, descripcion, es_automatico) VALUES
('PEDIDO_ENTREGADO', 'Ingreso automático por entrega de pedido', TRUE),
('INGRESO_MANUAL', 'Ingreso manual registrado por usuario', FALSE),
('PROPINA', 'Propinas de mensajeros', FALSE),
('OTROS_INGRESOS', 'Otros ingresos diversos', FALSE);

-- Insertar empresas de mensajeria (empresa_id = tenant_id)
INSERT IGNORE INTO empresa_mensajeria (tenant_id, nombre, direccion, telefono, email, estado_id) VALUES
(1, 'Mensajería Rápida S.A.S', 'Calle 45 #23-45, Bucaramanga', '3012345678', 'contacto@mensajeriarapida.com', 1),
(2, 'Express Delivery LTDA', 'Carrera 27 #34-12, Floridablanca', '3019876543', 'info@expressdelivery.com', 1),
(3, 'Domicilios Norte', 'Avenida Quebradaseca #15-30', '3156789012', 'contacto@domiciliosnorte.com', 2),
(4, 'MegaCourier S.A', 'Calle 50 #67-89, Bucaramanga', '3178901234', 'soporte@megacourier.com', 3),
(5, 'Eco Envíos SAS', 'Carrera 35 #45-67, Floridablanca', '3165432109', 'admin@ecoenvios.com', 3);

-- Insertar usuarios (mensajeria_id = tenant_id)
INSERT IGNORE INTO usuario (tenant_id, mensajeria_id, nombre_usuario, nombres, apellidos, email, password, rol_id, estado_id) VALUES
-- Administradores
-- Se quito el admin default para tenant 1
(2, 2, 'admin2', 'María Fernanda', 'González López', 'admin@expressdelivery.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, 1),
(3, 3, 'admin3', 'José Luis', 'Martínez Silva', 'admin@domiciliosnorte.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, 2),

-- Operadores
(1, 1, 'operador1', 'Ana Patricia', 'Vásquez Ruiz', 'operador1@mensajeriarapida.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, 1),
(1, 1, 'operador2', 'Diego Fernando', 'Morales Castro', 'operador2@mensajeriarapida.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, 1),
(2, 2, 'operador3', 'Lucía Andrea', 'Herrera Jiménez', 'operador1@expressdelivery.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, 1),

-- Mensajeros
(1, 1, 'mensajero1', 'Juan Carlos', 'Ramírez Gómez', 'mensajero1@mensajeriarapida.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1),
(1, 1, 'mensajero2', 'Pedro Luis', 'Sánchez Torres', 'mensajero2@mensajeriarapida.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1),
(1, 1, 'mensajero3', 'Andrés Felipe', 'Vargas Medina', 'mensajero3@mensajeriarapida.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1),
(2, 2, 'mensajero4', 'Miguel Ángel', 'Díaz Rojas', 'mensajero1@expressdelivery.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1),
(2, 2, 'mensajero5', 'Roberto Carlos', 'Pineda Uribe', 'mensajero2@expressdelivery.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1),
(3, 3, 'mensajero6', 'César Augusto', 'Mejía Cárdenas', 'mensajero1@domiciliosnorte.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 1);

-- Insertar direcciones
INSERT IGNORE INTO direccion (tenant_id, ciudad, barrio, direccion_completa, es_recogida, es_entrega, estado_id) VALUES
-- Direcciones de recogida
(1, 'Bucaramanga', 'Centro', 'Carrera 33 #45-67', TRUE, FALSE, 1),
(1, 'Bucaramanga', 'La Aurora', 'Calle 56 #23-45', TRUE, FALSE, 1),
(1, 'Bucaramanga', 'Cabecera', 'Carrera 15 #78-90', TRUE, FALSE, 1),
(1, 'Bucaramanga', 'Centro', 'Avenida Santander #12-34', TRUE, FALSE, 1),
(2, 'Floridablanca', 'La Cumbre', 'Calle 42 #56-78', TRUE, FALSE, 1),
(2, 'Floridablanca', 'Cañaveral', 'Carrera 27 #34-56', TRUE, FALSE, 1),
(3, 'Bucaramanga', 'Norte', 'Calle 85 #12-45', TRUE, FALSE, 1),

-- Direcciones de entrega
(1, 'Bucaramanga', 'Álamos', 'Calle 12 #34-56, Apto 301', FALSE, TRUE, 1),
(1, 'Bucaramanga', 'García Rovira', 'Carrera 45 #67-89', FALSE, TRUE, 1),
(1, 'Bucaramanga', 'Centro', 'Avenida Santander #123-45', FALSE, TRUE, 1),
(1, 'Bucaramanga', 'Provenza', 'Calle 78 #90-12, Casa 15', FALSE, TRUE, 1),
(1, 'Bucaramanga', 'Cabecera', 'Carrera 19 #23-45', FALSE, TRUE, 1),
(2, 'Bucaramanga', 'Universidad', 'Calle Universidad #45-67', FALSE, TRUE, 1),
(3, 'Bucaramanga', 'Real de Minas', 'Carrera 50 #78-90', FALSE, TRUE, 1);

-- Insertar clientes (mensajeria_id = tenant_id)
INSERT IGNORE INTO cliente (tenant_id, mensajeria_id, nombre, telefono, frecuencia_pedidos, descuento_porcentaje, estado_id) VALUES
(1, 1, 'Restaurante El Sabor', '3201234567', 45, 5.00, 1),
(1, 1, 'Farmacia San Rafael', '3109876543', 32, 3.00, 1),
(1, 1, 'Tienda Don Pepe', '3187654321', 28, 2.50, 1),
(1, 1, 'Supermercado La Canasta', '3156789012', 67, 7.50, 1),
(2, 2, 'Pastelería Dulce Hogar', '3143216789', 23, 2.00, 1),
(2, 2, 'Librería El Conocimiento', '3198765432', 15, 1.50, 1),
(3, 3, 'Pizzería Napolitana', '3165432109', 41, 4.00, 1);

-- Insertar relaciones cliente-direccion
INSERT IGNORE INTO cliente_direccion (cliente_id, direccion_id, es_predeterminada_recogida, es_predeterminada_entrega) VALUES
(1, 1, TRUE, FALSE),  -- Restaurante El Sabor - dirección recogida
(1, 8, FALSE, TRUE),  -- Restaurante El Sabor - dirección entrega predeterminada
(2, 2, TRUE, FALSE),  -- Farmacia San Rafael
(2, 9, FALSE, TRUE),
(3, 3, TRUE, FALSE),  -- Tienda Don Pepe
(3, 10, FALSE, TRUE),
(4, 4, TRUE, FALSE),  -- Supermercado La Canasta
(4, 11, FALSE, TRUE),
(5, 5, TRUE, FALSE),  -- Pastelería Dulce Hogar
(5, 12, FALSE, TRUE),
(6, 6, TRUE, FALSE),  -- Librería El Conocimiento
(6, 13, FALSE, TRUE),
(7, 7, TRUE, FALSE),  -- Pizzería Napolitana
(7, 14, FALSE, TRUE);

-- Insertar tarifas (mensajeria_id = tenant_id)
INSERT IGNORE INTO tarifa (tenant_id, mensajeria_id, nombre, valor_fijo, descripcion, activa) VALUES
-- Tarifas para Mensajería Rápida S.A.S (tenant_id = 1)
(1, 1, 'Express Zona 1', 8000.00, 'Entrega express en zona central', TRUE),
(1, 1, 'Express Zona 2', 10000.00, 'Entrega express zona intermedia', TRUE),
(1, 1, 'Express Zona 3', 12000.00, 'Entrega express zona lejana', TRUE),
(1, 1, 'Normal Zona 1', 5000.00, 'Entrega normal zona central', TRUE),
(1, 1, 'Normal Zona 2', 6500.00, 'Entrega normal zona intermedia', TRUE),
(1, 1, 'Normal Zona 3', 8000.00, 'Entrega normal zona lejana', TRUE),
(1, 1, 'Documentos', 4000.00, 'Envío de documentos', TRUE),
(1, 1, 'Medicamentos', 7000.00, 'Entrega de medicamentos', TRUE),

-- Tarifas para Express Delivery LTDA (tenant_id = 2)
(2, 2, 'Express Premium', 9000.00, 'Servicio express premium', TRUE),
(2, 2, 'Express Estándar', 7500.00, 'Servicio express estándar', TRUE),
(2, 2, 'Normal Premium', 6000.00, 'Servicio normal premium', TRUE),
(2, 2, 'Normal Estándar', 4500.00, 'Servicio normal estándar', TRUE),
(2, 2, 'Compras Express', 12000.00, 'Compra y entrega express', TRUE),
(2, 2, 'Compras Normal', 8500.00, 'Compra y entrega normal', TRUE),

-- Tarifas para Domicilios Norte (tenant_id = 3)
(3, 3, 'Express Norte', 8500.00, 'Entrega express zona norte', TRUE),
(3, 3, 'Normal Norte', 5500.00, 'Entrega normal zona norte', TRUE),
(3, 3, 'Especial Medicamentos', 7500.00, 'Entrega especializada medicamentos', TRUE),
(3, 3, 'Compra Express Norte', 11000.00, 'Compra y entrega express norte', TRUE);

-- Insertar pedidos de ejemplo (mensajeria_id = tenant_id)
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas) VALUES

-- Pedidos para Mensajería Rápida S.A.S (tenant_id = 1, mensajeria_id = 1)
(1, 1, 1, 7, 1, 1, 1, 8, '3201234567', '3111234567', 'Comida preparada', 2.50, 45000.00, 0.00, 8000.00, 8000.00, 3, 45, 'Entregar en recepción, apartamento 301'),
(1, 2, 1, 8, 2, 4, 2, 9, '3109876543', '3122345678', 'Medicamentos', 0.50, 85000.00, 0.00, 5000.00, 5000.00, 4, NULL, 'Pedido entregado exitosamente'),
(1, 3, 1, 7, 1, 2, 3, 10, '3187654321', '3133456789', 'Productos varios', 3.20, 120000.00, 0.00, 10000.00, 10000.00, 2, NULL, 'Cliente prefiere entrega después de las 3 PM'),
(1, 4, 1, NULL, 2, 5, 4, 11, '3156789012', '3144567890', 'Víveres', 5.80, 230000.00, 0.00, 6500.00, 6500.00, 1, NULL, 'Mercado semanal, manejar con cuidado'),

-- Pedidos para Express Delivery LTDA (tenant_id = 2, mensajeria_id = 2)
(2, 5, 2, 10, 4, 13, 5, 12, '3143216789', '3155678901', 'Torta de cumpleaños', 2.00, 75000.00, 65000.00, 12000.00, 12000.00, 4, NULL, 'Compra realizada en Panadería Central'),
(2, 6, 2, 11, 6, 12, 6, 13, '3198765432', '3166789012', 'Libros académicos', 1.20, 180000.00, 0.00, 4500.00, 4500.00, 3, 30, 'Material de estudio universitario'),

-- Pedidos para Domicilios Norte (tenant_id = 3, mensajeria_id = 3)
(3, 7, 3, 12, 1, 17, 7, 14, '3165432109', '3177890123', 'Pizza familiar', 1.50, 42000.00, 0.00, 8500.00, 8500.00, 2, NULL, 'Pizza hawaiana, entregar caliente');

-- Pedidos sin cliente registrado (temporales)
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
					direccion_recogida_id, direccion_entrega_id, direccion_recogida_temporal, direccion_entrega_temporal, 
					ciudad_recogida, barrio_recogida, ciudad_entrega, barrio_entrega, telefono_recogida, telefono_entrega, 
					tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, tiempo_entrega_minutos, notas) VALUES 
-- Pedido sin cliente para Mensajería Rápida S.A.S (tenant_id = 1)
(1, NULL, 1, 9, 6, 7, NULL, NULL, 'Calle 40 #25-30, Oficina 201', 'Carrera 50 #60-40, Casa blanca',  'Bucaramanga', 'Centro', 'Bucaramanga', 'Provenza', '3201111111', '3202222222', 'Documentos legales', 0.20, 150000.00, 0.00, 4000.00, 4000.00, 4, NULL, 'Contratos importantes, manejar con cuidado'),

-- Pedido sin cliente para Express Delivery LTDA (tenant_id = 2)
(2, NULL, 2, NULL, 8, 14, NULL, NULL, 'Centro Comercial La Quinta, Local 45', 'Avenida Los Estudiantes #78-90', 'Bucaramanga', 'Centro', 'Bucaramanga', 'Universidad', '3203333333', '3204444444', 'Comida china', 2.80, 35000.00, 28000.00, 8500.00, 8500.00, 1, NULL, 'Compra en restaurante Golden Dragon');

-- Insertar historial de pedidos
INSERT IGNORE INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id) VALUES
(1, 1, 'pendiente', 'asignado', 4),      -- operador1
(1, 1, 'asignado', 'en_transito', 4),   -- operador1 
(1, 1, 'en_transito', 'entregado', 4),  -- operador1
(2, 1, 'pendiente', 'asignado', 4),      -- operador1
(2, 1, 'asignado', 'en_transito', 5),   -- operador2
(2, 1, 'en_transito', 'entregado', 5),  -- operador2
(3, 1, 'pendiente', 'asignado', 4),      -- operador1
(4, 3, 'NULL', '7', 5),                  -- operador2 asignó mensajero_id = 7
(5, 1, 'pendiente', 'asignado', 6),      -- operador3
(5, 1, 'asignado', 'en_transito', 6),   -- operador3 
(5, 1, 'en_transito', 'entregado', 6),  -- operador3
(6, 1, 'pendiente', 'asignado', 6),      -- operador3
(6, 1, 'asignado', 'en_transito', 5),   -- operador2 
(7, 1, 'pendiente', 'asignado', 3),      -- admin3
(8, 1, 'pendiente', 'asignado', 4),      -- operador1
(8, 1, 'asignado', 'en_transito', 5),   -- operador2 
(8, 1, 'en_transito', 'entregado', 5);  -- operador2

-- Insertar arqueos de caja (mensajeria_id = tenant_id)
INSERT IGNORE INTO arqueo_caja (tenant_id, mensajeria_id, usuario_id, fecha, turno_id, efectivo_inicio, egresos, efectivo_real, estado_id, observaciones) VALUES
-- Arqueos para Mensajería Rápida S.A.S (tenant_id = 1)
(1, 1, 4, '2024-12-20', 1, 50000.00, 5000.00, 68000.00, 2, 'Arqueo turno mañana - Sin diferencias'),
(1, 1, 5, '2024-12-20', 2, 68000.00, 8000.00, 89500.00, 2, 'Arqueo turno tarde - Todo en orden'),
(1, 1, 4, '2024-12-21', 1, 45000.00, 3000.00, 67000.00, 2, 'Arqueo turno mañana'),

-- Arqueos para Express Delivery LTDA (tenant_id = 2)
(2, 2, 6, '2024-12-20', 1, 40000.00, 4000.00, 56500.00, 2, 'Arqueo matutino completo'),
(2, 2, 6, '2024-12-20', 2, 56500.00, 6500.00, 78000.00, 3, 'Diferencia detectada en arqueo vespertino'),

-- Arqueos para Domicilios Norte (tenant_id = 3)
(3, 3, 3, '2024-12-20', 1, 35000.00, 2500.00, 51000.00, 2, 'Primer arqueo del día - Normal'),
(3, 3, 3, '2024-12-21', 1, 30000.00, 1500.00, 45500.00, 1, 'Arqueo en proceso');

-- Insertar ingresos de arqueo
INSERT IGNORE INTO ingreso_arqueo (arqueo_id, tipo_ingreso_id, pedido_id, monto, descripcion) VALUES
-- Ingresos para arqueo 1 (Mensajería Rápida - 2024-12-20 mañana)
(1, 1, 1, 8000.00, 'Ingreso automático por entrega pedido #1'),
(1, 1, 2, 5000.00, 'Ingreso automático por entrega pedido #2'),
(1, 2, NULL, 10000.00, 'Pago adicional cliente frecuente'),

-- Ingresos para arqueo 2 (Mensajería Rápida - 2024-12-20 tarde)
(2, 1, 3, 10000.00, 'Ingreso automático por entrega pedido #3'),
(2, 3, NULL, 7500.00, 'Propinas mensajeros turno tarde'),
(2, 2, NULL, 12000.00, 'Servicios especiales domingo'),

-- Ingresos para arqueo 3 (Mensajería Rápida - 2024-12-21 mañana)
(3, 2, NULL, 15000.00, 'Pago servicios nocturnos'),
(3, 4, NULL, 10000.00, 'Ingresos varios del día anterior'),

-- Ingresos para arqueo 4 (Express Delivery - 2024-12-20 mañana)
(4, 1, 5, 12000.00, 'Ingreso automático por entrega pedido #5'),
(4, 1, 8, 4000.00, 'Ingreso automático por entrega pedido #8'),
(4, 2, NULL, 4500.00, 'Servicios adicionales'),

-- Ingresos para arqueo 5 (Express Delivery - 2024-12-20 tarde)
(5, 1, 6, 4500.00, 'Ingreso automático por entrega pedido #6'),
(5, 2, NULL, 8000.00, 'Pagos pendientes del día anterior'),
(5, 3, NULL, 9500.00, 'Propinas y bonificaciones'),

-- Ingresos para arqueo 6 (Domicilios Norte - 2024-12-20 mañana)
(6, 2, NULL, 18500.00, 'Ingresos varios del turno'),
(6, 3, NULL, 5000.00, 'Propinas mensajeros'),

-- Ingresos para arqueo 7 (Domicilios Norte - 2024-12-21 mañana - en proceso)
(7, 2, NULL, 17000.00, 'Ingresos parciales del turno');

-- Insertar notificaciones
INSERT IGNORE INTO notificacion (tenant_id, usuario_id, tipo_notificacion_id, titulo, mensaje, leida) VALUES
-- Notificaciones para mensajeros
(1, 7, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #1 - Restaurante El Sabor', TRUE),
(1, 8, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #2 - Farmacia San Rafael', TRUE),
(1, 7, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #3 - Tienda Don Pepe', FALSE),
(2, 10, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #5 - Pastelería Dulce Hogar', TRUE),
(2, 11, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #6 - Librería El Conocimiento', FALSE),
(3, 12, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #7 - Pizzería Napolitana', FALSE),

-- Notificaciones para operadores y admins
(1, 4, 4, 'Pedido completado', 'El pedido #1 ha sido entregado exitosamente', TRUE),
(1, 4, 4, 'Pedido completado', 'El pedido #2 ha sido entregado exitosamente', TRUE),
(2, 6, 4, 'Pedido completado', 'El pedido #5 ha sido entregado exitosamente', FALSE),
(2, 1, 1, 'Diferencia en arqueo', 'Se detectó diferencia en arqueo del 2024-12-20 turno tarde - Express Delivery', FALSE),
(3, 3, 1, 'Arqueo pendiente', 'Recuerda cerrar el arqueo del turno actual', FALSE),

-- Notificaciones de cambios en pedidos
(1, 7, 3, 'Cambio en pedido', 'El pedido #3 ha sido modificado - revisar notas', FALSE),
(1, 5, 3, 'Pedido reasignado', 'El pedido #4 requiere asignación de mensajero', FALSE),
(3, 12, 3, 'Actualización pedido', 'El pedido #7 ha sido actualizado por el operador', FALSE);

-- Actualizar algunos campos calculados y fechas
UPDATE cliente SET 
    ultimo_pedido = '2024-12-21 14:30:00',
    frecuencia_pedidos = frecuencia_pedidos + 1
WHERE id IN (1, 2, 5, 6);

UPDATE mensajero SET 
    fecha_ultima_entrega = '2024-12-21 16:45:00',
    total_entregas = total_entregas + 1
WHERE id IN (7, 8, 10);

-- Actualizar fechas de entrega para pedidos completados
UPDATE pedido SET fecha_entrega = '2024-12-21 15:20:00' WHERE id = 1;
UPDATE pedido SET fecha_entrega = '2024-12-21 11:45:00' WHERE id = 2;
UPDATE pedido SET fecha_entrega = '2024-12-21 17:30:00' WHERE id = 5;
UPDATE pedido SET fecha_entrega = '2024-12-21 10:15:00' WHERE id = 8;

-- Inserción datos tenant 1
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

-- Diciembre 2024 - Pedidos 10-13
(1, 1, 1, 7, 2, 4, 1, 8, '3201234567', '3111234567', 'Almuerzo ejecutivo', 1.80, 35000.00, 0.00, 5000.00, 5000.00, 4, 35, 'Entregado en oficina', '2024-12-22 12:15:00', '2024-12-22 12:50:00'),
(1, 3, 1, 8, 1, 1, 3, 10, '3187654321', '3133456789', 'Productos navideños', 4.50, 180000.00, 0.00, 8000.00, 8000.00, 4, 55, 'Decoraciones navideñas', '2024-12-23 15:20:00', '2024-12-23 16:15:00'),
(2, 1, 2, 10, 1, 9, 5, 12, '3143216789', '3155678901', 'Regalos navideños', 3.20, 220000.00, 0.00, 9000.00, 9000.00, 4, 42, 'Envueltos para regalo', '2024-12-24 10:30:00', '2024-12-24 11:12:00'),
(2, 2, 2, 11, 2, 12, 6, 13, '3198765432', '3166789012', 'Material escolar', 2.10, 95000.00, 0.00, 4500.00, 4500.00, 4, 28, 'Fin de año escolar', '2024-12-28 14:45:00', '2024-12-28 15:13:00');

-- ENERO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES
                   
(1, 2, 1, 9, 7, 8, 2, 9, '3109876543', '3122345678', 'Medicinas urgentes', 0.80, 125000.00, 0.00, 7000.00, 7000.00, 4, 25, 'Medicamento controlado', '2025-01-05 08:30:00', '2025-01-05 08:55:00'),
(1, 4, 1, 7, 2, 5, 4, 11, '3156789012', '3144567890', 'Compra semanal', 6.20, 280000.00, 0.00, 6500.00, 6500.00, 4, 48, 'Productos perecederos', '2025-01-12 16:20:00', '2025-01-12 17:08:00'),
(2, 3, 2, 10, 4, 13, 5, 12, '3143216789', '3155678901', 'Torta cumpleaños', 2.50, 85000.00, 75000.00, 12000.00, 12000.00, 4, 38, 'Compra en pastelería', '2025-01-18 14:15:00', '2025-01-18 14:53:00'),
(3, 4, 3, 12, 1, 17, 7, 14, '3165432109', '3177890123', 'Comida italiana', 2.80, 58000.00, 0.00, 8500.00, 8500.00, 4, 33, 'Pasta y pizza', '2025-01-25 19:40:00', '2025-01-25 20:13:00');

-- FEBRERO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 1, 1, 8, 1, 2, 1, 8, '3201234567', '3111234567', 'Cena romántica', 2.20, 95000.00, 0.00, 10000.00, 10000.00, 4, 47, 'San Valentín - manejar con cuidado', '2025-02-14 18:30:00', '2025-02-14 19:17:00'),
(1, 3, 1, 9, 6, 7, 3, 10, '3187654321', '3133456789', 'Documentos legales', 0.30, 200000.00, 0.00, 4000.00, 4000.00, 4, 22, 'Contratos importantes', '2025-02-20 10:15:00', '2025-02-20 10:37:00'),
(2, 1, 2, 11, 2, 12, 6, 13, '3198765432', '3166789012', 'Libros universitarios', 3.50, 450000.00, 0.00, 4500.00, 4500.00, 4, 31, 'Nuevo semestre', '2025-02-03 09:20:00', '2025-02-03 09:51:00'),
(3, 2, 3, 12, 1, 17, 7, 14, '3165432109', '3177890123', 'Desayuno especial', 1.20, 42000.00, 0.00, 8500.00, 8500.00, 4, 29, 'Desayuno continental', '2025-02-28 07:45:00', '2025-02-28 08:14:00');

-- MARZO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 2, 1, 7, 7, 8, 2, 9, '3109876543', '3122345678', 'Suplementos vitamínicos', 1.50, 180000.00, 0.00, 7000.00, 7000.00, 4, 26, 'Vitaminas del mes', '2025-03-08 11:20:00', '2025-03-08 11:46:00'),
(1, 4, 1, 8, 2, 6, 4, 11, '3156789012', '3144567890', 'Compras hogar', 7.80, 380000.00, 0.00, 8000.00, 8000.00, 4, 52, 'Productos de limpieza', '2025-03-15 13:40:00', '2025-03-15 14:32:00'),
(2, 3, 2, 10, 1, 9, 5, 12, '3143216789', '3155678901', 'Postres gourmet', 1.80, 120000.00, 0.00, 9000.00, 9000.00, 4, 36, 'Celebración familiar', '2025-03-22 15:10:00', '2025-03-22 15:46:00'),
(3, 4, 3, 12, 2, 18, 7, 14, '3165432109', '3177890123', 'Almuerzo familiar', 4.20, 85000.00, 0.00, 5500.00, 5500.00, 4, 41, 'Pizza y lasaña', '2025-03-29 13:25:00', '2025-03-29 14:06:00');

-- ABRIL 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 1, 1, 9, 1, 1, 1, 8, '3201234567', '3111234567', 'Catering evento', 8.50, 650000.00, 0.00, 8000.00, 8000.00, 4, 58, 'Evento corporativo', '2025-04-10 16:00:00', '2025-04-10 16:58:00'),
(1, 3, 1, 7, 2, 4, 3, 10, '3187654321', '3133456789', 'Artículos oficina', 3.20, 250000.00, 0.00, 5000.00, 5000.00, 4, 34, 'Material de oficina', '2025-04-17 10:30:00', '2025-04-17 11:04:00'),
(2, 2, 2, 11, 6, 12, 6, 13, '3198765432', '3166789012', 'Documentos académicos', 0.50, 80000.00, 0.00, 4500.00, 4500.00, 4, 24, 'Certificados y diplomas', '2025-04-05 14:20:00', '2025-04-05 14:44:00'),
(2, 4, 2, 10, 4, 13, 5, 12, '3143216789', '3155678901', 'Ingredientes especiales', 2.30, 150000.00, 130000.00, 12000.00, 12000.00, 4, 45, 'Compra en mercado gourmet', '2025-04-24 11:15:00', '2025-04-24 12:00:00');

-- MAYO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 2, 1, 8, 7, 8, 2, 9, '3109876543', '3122345678', 'Medicamentos crónicos', 0.90, 95000.00, 0.00, 7000.00, 7000.00, 4, 27, 'Tratamiento mensual', '2025-05-02 09:45:00', '2025-05-02 10:12:00'),
(1, 4, 1, 9, 2, 5, 4, 11, '3156789012', '3144567890', 'Despensa familiar', 9.20, 420000.00, 0.00, 6500.00, 6500.00, 4, 61, 'Compra quincenal', '2025-05-16 17:30:00', '2025-05-16 18:31:00'),
(2, 1, 2, 10, 1, 10, 5, 12, '3143216789', '3155678901', 'Torta día de la madre', 3.80, 180000.00, 0.00, 7500.00, 7500.00, 4, 39, 'Celebración especial', '2025-05-11 12:20:00', '2025-05-11 12:59:00'),
(3, 3, 3, 12, 1, 17, 7, 14, '3165432109', '3177890123', 'Cena italiana', 3.50, 95000.00, 0.00, 8500.00, 8500.00, 4, 44, 'Pasta artesanal', '2025-05-23 18:15:00', '2025-05-23 18:59:00');

-- JUNIO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 1, 1, 7, 1, 2, 1, 8, '3201234567', '3111234567', 'Comida día del padre', 4.20, 150000.00, 0.00, 10000.00, 10000.00, 4, 49, 'Celebración papá', '2025-06-15 13:00:00', '2025-06-15 13:49:00'),
(1, 3, 1, 8, 6, 7, 3, 10, '3187654321', '3133456789', 'Certificados digitales', 0.20, 120000.00, 0.00, 4000.00, 4000.00, 4, 18, 'Documentos urgentes', '2025-06-08 15:40:00', '2025-06-08 15:58:00'),
(2, 4, 2, 11, 2, 12, 6, 13, '3198765432', '3166789012', 'Libros vacaciones', 2.80, 320000.00, 0.00, 4500.00, 4500.00, 4, 32, 'Lectura de verano', '2025-06-20 11:30:00', '2025-06-20 12:02:00'),
(3, 2, 3, 12, 2, 18, 7, 14, '3165432109', '3177890123', 'Helados artesanales', 1.50, 65000.00, 0.00, 5500.00, 5500.00, 4, 25, 'Postres de verano', '2025-06-27 16:45:00', '2025-06-27 17:10:00');

-- JULIO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 2, 1, 9, 7, 8, 2, 9, '3109876543', '3122345678', 'Medicina deportiva', 1.20, 85000.00, 0.00, 7000.00, 7000.00, 4, 23, 'Suplementos deportivos', '2025-07-04 10:20:00', '2025-07-04 10:43:00'),
(1, 4, 1, 7, 2, 6, 4, 11, '3156789012', '3144567890', 'Productos verano', 5.60, 290000.00, 0.00, 8000.00, 8000.00, 4, 46, 'Protectores solares', '2025-07-11 14:15:00', '2025-07-11 15:01:00'),
(2, 1, 2, 10, 4, 13, 5, 12, '3143216789', '3155678901', 'Postre helado', 2.10, 95000.00, 85000.00, 12000.00, 12000.00, 4, 37, 'Compra en heladería', '2025-07-18 15:30:00', '2025-07-18 16:07:00'),
(2, 3, 2, 11, 1, 9, 6, 13, '3198765432', '3166789012', 'Material curso', 1.90, 180000.00, 0.00, 9000.00, 9000.00, 4, 41, 'Curso de verano', '2025-07-25 09:10:00', '2025-07-25 09:51:00');

-- AGOSTO 2025
INSERT IGNORE INTO pedido (tenant_id, cliente_id, mensajeria_id, mensajero_id, tipo_servicio_id, tarifa_id, 
                   direccion_recogida_id, direccion_entrega_id, telefono_recogida, telefono_entrega, 
                   tipo_paquete, peso_kg, valor_declarado, costo_compra, subtotal, total, estado_id, 
                   tiempo_entrega_minutos, notas, fecha_creacion, fecha_entrega) VALUES

(1, 1, 1, 8, 1, 1, 1, 8, '3201234567', '3111234567', 'Almuerzo de negocios', 3.20, 180000.00, 0.00, 8000.00, 8000.00, 3, 0, 'Reunión importante', '2025-08-01 12:30:00', NULL),
(1, 3, 1, 9, 2, 4, 3, 10, '3187654321', '3133456789', 'Útiles escolares', 2.80, 150000.00, 0.00, 5000.00, 5000.00, 2, 0, 'Regreso a clases', '2025-08-05 08:45:00', NULL),
(2, 4, 2, NULL, 1, 9, 5, 12, '3143216789', '3155678901', 'Torta cumpleaños', 2.50, 120000.00, 0.00, 9000.00, 9000.00, 1, 0, 'Pendiente asignación', '2025-08-06 10:15:00', NULL),
(3, 2, 3, 12, 2, 18, 7, 14, '3165432109', '3177890123', 'Almuerzo ejecutivo', 2.20, 75000.00, 0.00, 5500.00, 5500.00, 2, 0, 'Cliente VIP', '2025-08-06 11:20:00', NULL);

INSERT IGNORE INTO historial_pedido (pedido_id, tipo_cambio_id, valor_anterior, valor_nuevo, usuario_id) VALUES
-- Diciembre 2024
(10, 1, 'pendiente', 'asignado', 4),
(10, 1, 'asignado', 'en_transito', 4),
(10, 1, 'en_transito', 'entregado', 4),
(11, 1, 'pendiente', 'asignado', 5),
(11, 1, 'asignado', 'en_transito', 5),
(11, 1, 'en_transito', 'entregado', 5),
(12, 1, 'pendiente', 'asignado', 6),
(12, 1, 'asignado', 'en_transito', 6),
(12, 1, 'en_transito', 'entregado', 6),
(13, 1, 'pendiente', 'asignado', 6),
(13, 1, 'asignado', 'en_transito', 6),
(13, 1, 'en_transito', 'entregado', 6),

-- Enero 2025
(14, 1, 'pendiente', 'asignado', 4),
(14, 1, 'asignado', 'en_transito', 4),
(14, 1, 'en_transito', 'entregado', 4),
(15, 1, 'pendiente', 'asignado', 5),
(15, 1, 'asignado', 'en_transito', 5),
(15, 1, 'en_transito', 'entregado', 5),
(16, 1, 'pendiente', 'asignado', 6),
(16, 1, 'asignado', 'en_transito', 6),
(16, 1, 'en_transito', 'entregado', 6),
(17, 1, 'pendiente', 'asignado', 3),
(17, 1, 'asignado', 'en_transito', 3),
(17, 1, 'en_transito', 'entregado', 3),

-- Febrero 2025
(18, 1, 'pendiente', 'asignado', 4),
(18, 1, 'asignado', 'en_transito', 4),
(18, 1, 'en_transito', 'entregado', 4),
(19, 1, 'pendiente', 'asignado', 5),
(19, 1, 'asignado', 'en_transito', 5),
(19, 1, 'en_transito', 'entregado', 5),
(20, 1, 'pendiente', 'asignado', 6),
(20, 1, 'asignado', 'en_transito', 6),
(20, 1, 'en_transito', 'entregado', 6),
(21, 1, 'pendiente', 'asignado', 3),
(21, 1, 'asignado', 'en_transito', 3),
(21, 1, 'en_transito', 'entregado', 3),

-- Marzo 2025
(22, 1, 'pendiente', 'asignado', 4),
(22, 1, 'asignado', 'en_transito', 4),
(22, 1, 'en_transito', 'entregado', 4),
(23, 1, 'pendiente', 'asignado', 5),
(23, 1, 'asignado', 'en_transito', 5),
(23, 1, 'en_transito', 'entregado', 5),
(24, 1, 'pendiente', 'asignado', 6),
(24, 1, 'asignado', 'en_transito', 6),
(24, 1, 'en_transito', 'entregado', 6),
(25, 1, 'pendiente', 'asignado', 3),
(25, 1, 'asignado', 'en_transito', 3),
(25, 1, 'en_transito', 'entregado', 3),

-- Abril 2025
(26, 1, 'pendiente', 'asignado', 4),
(26, 1, 'asignado', 'en_transito', 4),
(26, 1, 'en_transito', 'entregado', 4),
(27, 1, 'pendiente', 'asignado', 5),
(27, 1, 'asignado', 'en_transito', 5),
(27, 1, 'en_transito', 'entregado', 5),
(28, 1, 'pendiente', 'asignado', 6),
(28, 1, 'asignado', 'en_transito', 6),
(28, 1, 'en_transito', 'entregado', 6),
(29, 1, 'pendiente', 'asignado', 6),
(29, 1, 'asignado', 'en_transito', 6),
(29, 1, 'en_transito', 'entregado', 6),

-- Mayo 2025
(30, 1, 'pendiente', 'asignado', 4),
(30, 1, 'asignado', 'en_transito', 4),
(30, 1, 'en_transito', 'entregado', 4),
(31, 1, 'pendiente', 'asignado', 5),
(31, 1, 'asignado', 'en_transito', 5),
(31, 1, 'en_transito', 'entregado', 5),
(32, 1, 'pendiente', 'asignado', 6),
(32, 1, 'asignado', 'en_transito', 6),
(32, 1, 'en_transito', 'entregado', 6),
(33, 1, 'pendiente', 'asignado', 3),
(33, 1, 'asignado', 'en_transito', 3),
(33, 1, 'en_transito', 'entregado', 3),

-- Junio 2025
(34, 1, 'pendiente', 'asignado', 4),
(34, 1, 'asignado', 'en_transito', 4),
(34, 1, 'en_transito', 'entregado', 4),
(35, 1, 'pendiente', 'asignado', 5),
(35, 1, 'asignado', 'en_transito', 5),
(35, 1, 'en_transito', 'entregado', 5),
(36, 1, 'pendiente', 'asignado', 6),
(36, 1, 'asignado', 'en_transito', 6),
(36, 1, 'en_transito', 'entregado', 6),
(37, 1, 'pendiente', 'asignado', 3),
(37, 1, 'asignado', 'en_transito', 3),
(37, 1, 'en_transito', 'entregado', 3),

-- Julio 2025
(38, 1, 'pendiente', 'asignado', 4),
(38, 1, 'asignado', 'en_transito', 4),
(38, 1, 'en_transito', 'entregado', 4),
(39, 1, 'pendiente', 'asignado', 5),
(39, 1, 'asignado', 'en_transito', 5),
(39, 1, 'en_transito', 'entregado', 5),
(40, 1, 'pendiente', 'asignado', 6),
(40, 1, 'asignado', 'en_transito', 6),
(40, 1, 'en_transito', 'entregado', 6),
(41, 1, 'pendiente', 'asignado', 6),
(41, 1, 'asignado', 'en_transito', 6),
(41, 1, 'en_transito', 'entregado', 6),

-- Agosto 2025 (solo cambios para pedidos activos)
(42, 1, 'pendiente', 'asignado', 4),
(42, 1, 'asignado', 'en_transito', 4),
(43, 1, 'pendiente', 'asignado', 5),
(45, 1, 'pendiente', 'asignado', 3);


INSERT IGNORE INTO arqueo_caja (tenant_id, mensajeria_id, usuario_id, fecha, turno_id, efectivo_inicio, egresos, efectivo_real, estado_id, observaciones) VALUES

-- Diciembre 2024
(1, 1, 4, '2024-12-22', 1, 45000.00, 3500.00, 75500.00, 2, 'Arqueo con pedidos navideños'),
(1, 1, 5, '2024-12-22', 2, 75500.00, 4200.00, 96300.00, 2, 'Turno tarde diciembre'),
(1, 1, 6, '2024-12-23', 1, 38000.00, 2800.00, 59500.00, 2, 'Ventas pre-navideñas'),
(1, 1, 6, '2024-12-24', 1, 42000.00, 3200.00, 62700.00, 2, 'Nochebuena - servicio especial'),
(1, 1, 3, '2024-12-28', 2, 35000.00, 2100.00, 43500.00, 2, 'Semana entre festividades'),

-- Enero 2025
(1, 1, 4, '2025-01-05', 1, 50000.00, 4800.00, 77800.00, 2, 'Inicio de año'),
(1, 1, 5, '2025-01-12', 2, 52000.00, 5200.00, 85200.00, 2, 'Semana laboral normal'),
(1, 1, 6, '2025-01-18', 1, 44000.00, 3600.00, 59600.00, 2, 'Compras enero'),
(1, 1, 3, '2025-01-25', 2, 38000.00, 2900.00, 46900.00, 2, 'Fin de enero'),

-- Febrero 2025
(1, 1, 4, '2025-02-14', 2, 48000.00, 4500.00, 66500.00, 2, 'San Valentín - alta demanda'),
(1, 1, 5, '2025-02-20', 1, 51000.00, 3800.00, 59800.00, 2, 'Documentos importantes'),
(1, 1, 6, '2025-02-03', 1, 41000.00, 2900.00, 48400.00, 2, 'Inicio semestre universitario'),
(1, 1, 3, '2025-02-28', 1, 36000.00, 2400.00, 47000.00, 2, 'Fin de febrero'),

-- Marzo 2025
(1, 1, 4, '2025-03-08', 1, 49000.00, 4100.00, 70100.00, 2, 'Suplementos y compras'),
(1, 1, 5, '2025-03-15', 2, 53000.00, 5800.00, 81000.00, 2, 'Productos de limpieza'),
(1, 1, 6, '2025-03-22', 1, 43000.00, 3400.00, 55400.00, 2, 'Postres gourmet'),
(1, 1, 3, '2025-03-29', 2, 37000.00, 2700.00, 45200.00, 2, 'Fin de mes'),

-- Abril 2025
(1, 1, 4, '2025-04-10', 2, 55000.00, 6200.00, 87200.00, 2, 'Catering eventos'),
(1, 1, 5, '2025-04-17', 1, 47000.00, 3900.00, 55900.00, 2, 'Material oficina'),
(1, 1, 6, '2025-04-05', 1, 40000.00, 2800.00, 47300.00, 2, 'Documentos académicos'),
(1, 1, 6, '2025-04-24', 2, 48000.00, 4300.00, 72000.00, 2, 'Compra ingredientes especiales'),

-- Mayo 2025
(1, 1, 4, '2025-05-02', 1, 46000.00, 3600.00, 56600.00, 2, 'Medicamentos crónicos'),
(1, 1, 5, '2025-05-16', 2, 54000.00, 6500.00, 88000.00, 2, 'Despensa familiar grande'),
(1, 1, 6, '2025-05-11', 1, 42000.00, 3200.00, 52700.00, 2, 'Día de la madre'),
(1, 1, 3, '2025-05-23', 2, 39000.00, 2800.00, 51300.00, 2, 'Cena italiana'),

-- Junio 2025
(1, 1, 4, '2025-06-15', 2, 52000.00, 4800.00, 72000.00, 2, 'Día del padre'),
(1, 1, 5, '2025-06-08', 1, 45000.00, 2200.00, 49200.00, 2, 'Documentos digitales'),
(1, 1, 6, '2025-06-20', 1, 44000.00, 3100.00, 51600.00, 2, 'Libros de verano'),
(1, 1, 3, '2025-06-27', 2, 38000.00, 2300.00, 46800.00, 2, 'Helados artesanales'),

-- Julio 2025
(1, 1, 4, '2025-07-04', 1, 47000.00, 3400.00, 53400.00, 2, 'Medicina deportiva'),
(1, 1, 5, '2025-07-11', 2, 51000.00, 5100.00, 79100.00, 2, 'Productos de verano'),
(1, 1, 6, '2025-07-18', 2, 46000.00, 4200.00, 66700.00, 2, 'Compra heladería'),
(1, 1, 6, '2025-07-25', 1, 43000.00, 3700.00, 59000.00, 2, 'Material curso verano'),

-- Agosto 2025 (arqueos abiertos para pedidos actuales)
(1, 1, 4, '2025-08-01', 2, 50000.00, 1200.00, 51200.00, 1, 'Arqueo en proceso - almuerzo negocios'),
(1, 1, 6, '2025-08-06', 1, 45000.00, 800.00, 45800.00, 1, 'Arqueo abierto - pedido pendiente');

INSERT IGNORE INTO ingreso_arqueo (arqueo_id, tipo_ingreso_id, pedido_id, monto, descripcion) VALUES

-- Diciembre 2024 - Arqueo 8
(8, 1, 10, 5000.00, 'Ingreso automático por entrega pedido #10'),
(8, 1, 11, 8000.00, 'Ingreso automático por entrega pedido #11'),
(8, 2, NULL, 20000.00, 'Servicios especiales navideños'),

-- Diciembre 2024 - Arqueo 9
(9, 1, 12, 9000.00, 'Ingreso automático por entrega pedido #12'),
(9, 1, 13, 4500.00, 'Ingreso automático por entrega pedido #13'),
(9, 3, NULL, 8000.00, 'Propinas época navideña'),

-- Diciembre 2024 - Arqueo 10
(10, 1, 12, 9000.00, 'Ingreso automático por entrega pedido #12'),
(10, 2, NULL, 12500.00, 'Servicios adicionales diciembre'),

-- Diciembre 2024 - Arqueo 11
(11, 1, 13, 4500.00, 'Ingreso automático por entrega pedido #13'),
(11, 4, NULL, 16200.00, 'Otros ingresos navideños'),

-- Diciembre 2024 - Arqueo 12
(12, 4, NULL, 8500.00, 'Ingresos varios del día'),

-- Enero 2025 - Arqueo 13
(13, 1, 14, 7000.00, 'Ingreso automático por entrega pedido #14'),
(13, 1, 15, 6500.00, 'Ingreso automático por entrega pedido #15'),
(13, 2, NULL, 14300.00, 'Servicios enero'),

-- Enero 2025 - Arqueo 14
(14, 1, 16, 12000.00, 'Ingreso automático por entrega pedido #16'),
(14, 3, NULL, 25200.00, 'Propinas y bonificaciones'),

-- Enero 2025 - Arqueo 15
(15, 1, 16, 12000.00, 'Ingreso automático por entrega pedido #16'),
(15, 2, NULL, 3600.00, 'Servicios adicionales'),

-- Enero 2025 - Arqueo 16
(16, 1, 17, 8500.00, 'Ingreso automático por entrega pedido #17'),

-- Febrero 2025 - Arqueo 17
(17, 1, 18, 10000.00, 'Ingreso automático por entrega pedido #18'),
(17, 1, 19, 4000.00, 'Ingreso automático por entrega pedido #19'),
(17, 2, NULL, 4500.00, 'Servicios San Valentín'),

-- Febrero 2025 - Arqueo 18
(18, 1, 19, 4000.00, 'Ingreso automático por entrega pedido #19'),
(18, 4, NULL, 4800.00, 'Otros ingresos febrero'),

-- Febrero 2025 - Arqueo 19
(19, 1, 20, 4500.00, 'Ingreso automático por entrega pedido #20'),
(19, 2, NULL, 2900.00, 'Servicios universitarios'),

-- Febrero 2025 - Arqueo 20
(20, 1, 21, 8500.00, 'Ingreso automático por entrega pedido #21'),
(20, 3, NULL, 2500.00, 'Propinas febrero'),

-- Marzo 2025 - Arqueo 21
(21, 1, 22, 7000.00, 'Ingreso automático por entrega pedido #22'),
(21, 1, 23, 8000.00, 'Ingreso automático por entrega pedido #23'),
(21, 2, NULL, 6100.00, 'Servicios marzo'),

-- Marzo 2025 - Arqueo 22
(22, 1, 23, 8000.00, 'Ingreso automático por entrega pedido #23'),
(22, 3, NULL, 20000.00, 'Propinas productos limpieza'),

-- Marzo 2025 - Arqueo 23
(23, 1, 24, 9000.00, 'Ingreso automático por entrega pedido #24'),
(23, 2, NULL, 3400.00, 'Servicios gourmet'),

-- Marzo 2025 - Arqueo 24
(24, 1, 25, 5500.00, 'Ingreso automático por entrega pedido #25'),
(24, 4, NULL, 2700.00, 'Otros ingresos marzo'),

-- Abril 2025 - Arqueo 25
(25, 1, 26, 8000.00, 'Ingreso automático por entrega pedido #26'),
(25, 1, 27, 5000.00, 'Ingreso automático por entrega pedido #27'),
(25, 2, NULL, 24200.00, 'Catering y eventos'),

-- Abril 2025 - Arqueo 26
(26, 1, 27, 5000.00, 'Ingreso automático por entrega pedido #27'),
(26, 2, NULL, 3900.00, 'Material oficina'),

-- Abril 2025 - Arqueo 27
(27, 1, 28, 4500.00, 'Ingreso automático por entrega pedido #28'),
(27, 2, NULL, 2800.00, 'Documentos académicos'),

-- Abril 2025 - Arqueo 28
(28, 1, 29, 12000.00, 'Ingreso automático por entrega pedido #29'),
(28, 2, NULL, 12000.00, 'Compras especializadas'),

-- Mayo 2025 - Arqueo 29
(29, 1, 30, 7000.00, 'Ingreso automático por entrega pedido #30'),
(29, 2, NULL, 3600.00, 'Medicamentos crónicos'),

-- Mayo 2025 - Arqueo 30
(30, 1, 31, 6500.00, 'Ingreso automático por entrega pedido #31'),
(30, 3, NULL, 27500.00, 'Propinas despensa grande'),

-- Mayo 2025 - Arqueo 31
(31, 1, 32, 7500.00, 'Ingreso automático por entrega pedido #32'),
(31, 2, NULL, 3200.00, 'Día de la madre'),

-- Mayo 2025 - Arqueo 32
(32, 1, 33, 8500.00, 'Ingreso automático por entrega pedido #33'),
(32, 2, NULL, 4800.00, 'Cena italiana'),

-- Junio 2025 - Arqueo 33
(33, 1, 34, 10000.00, 'Ingreso automático por entrega pedido #34'),
(33, 1, 35, 4000.00, 'Ingreso automático por entrega pedido #35'),
(33, 2, NULL, 8000.00, 'Día del padre'),

-- Junio 2025 - Arqueo 34
(34, 1, 35, 4000.00, 'Ingreso automático por entrega pedido #35'),
(34, 4, NULL, 2200.00, 'Documentos digitales'),

-- Junio 2025 - Arqueo 35
(35, 1, 36, 4500.00, 'Ingreso automático por entrega pedido #36'),
(35, 2, NULL, 3100.00, 'Libros verano'),

-- Junio 2025 - Arqueo 36
(36, 1, 37, 5500.00, 'Ingreso automático por entrega pedido #37'),
(36, 3, NULL, 3000.00, 'Propinas helados'),

-- Julio 2025 - Arqueo 37
(37, 1, 38, 7000.00, 'Ingreso automático por entrega pedido #38'),
(37, 2, NULL, 3400.00, 'Medicina deportiva'),

-- Julio 2025 - Arqueo 38
(38, 1, 39, 8000.00, 'Ingreso automático por entrega pedido #39'),
(38, 3, NULL, 23100.00, 'Propinas productos verano'),

-- Julio 2025 - Arqueo 39
(39, 1, 40, 12000.00, 'Ingreso automático por entrega pedido #40'),
(39, 2, NULL, 8500.00, 'Compra heladería'),

-- Julio 2025 - Arqueo 40
(40, 1, 41, 9000.00, 'Ingreso automático por entrega pedido #41'),
(40, 2, NULL, 7000.00, 'Material curso'),

-- Agosto 2025 - Arqueo 41 (en proceso)
(41, 2, NULL, 1200.00, 'Servicios parciales agosto'),

-- Agosto 2025 - Arqueo 42 (en proceso)
(42, 2, NULL, 800.00, 'Ingresos pendientes');


-- Actualizar estadísticas para mensajeros con entregas completadas
UPDATE mensajero SET 
    fecha_ultima_entrega = (
        SELECT MAX(fecha_entrega) 
        FROM pedido 
        WHERE mensajero_id = mensajero.id AND estado_id = 4
    )
WHERE id IN (7, 8, 9, 10, 11, 12);

-- Actualizar total de entregas por mensajero
UPDATE mensajero SET 
    total_entregas = (
        SELECT COUNT(*) 
        FROM pedido 
        WHERE mensajero_id = mensajero.id AND estado_id = 4
    );

-- Actualizar último pedido y frecuencia para cada cliente
UPDATE cliente c SET 
    c.ultimo_pedido = (
        SELECT MAX(p.fecha_creacion) 
        FROM pedido p 
        WHERE p.cliente_id = c.id
    ),
    c.frecuencia_pedidos = (
        SELECT COUNT(*) 
        FROM pedido p 
        WHERE p.cliente_id = c.id
    )
WHERE c.id IN (1, 2, 3, 4, 5, 6, 7);

INSERT IGNORE INTO notificacion (tenant_id, usuario_id, tipo_notificacion_id, titulo, mensaje, leida) VALUES

-- Notificaciones de pedidos completados recientes
(1, 4, 4, 'Pedidos completados', 'Se han completado 8 pedidos en lo que va del mes', FALSE),
(2, 6, 4, 'Pedidos completados', 'Se han completado 6 pedidos en julio', TRUE),
(3, 3, 4, 'Pedidos completados', 'Se han completado 4 pedidos en julio', FALSE),

-- Notificaciones de arqueos
(1, 4, 1, 'Arqueo cerrado', 'Arqueo del 2025-07-25 cerrado exitosamente', TRUE),
(2, 6, 1, 'Arqueo cerrado', 'Arqueo del 2025-07-25 cerrado exitosamente', TRUE),
(1, 4, 1, 'Arqueo abierto', 'Recuerda cerrar el arqueo del turno actual', FALSE),

-- Notificaciones para mensajeros sobre nuevas asignaciones
(1, 8, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #42 - Almuerzo de negocios', FALSE),
(1, 9, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #43 - Útiles escolares', FALSE),
(3, 12, 2, 'Nuevo pedido asignado', 'Se te ha asignado el pedido #45 - Almuerzo ejecutivo', FALSE);