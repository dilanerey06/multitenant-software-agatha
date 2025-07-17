-- SCHEMA TENANT
CREATE SCHEMA tenant;
USE tenant;

-- 1. ESTADOS PARA TENANT
CREATE TABLE estado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. PLANES PARA TENANT
CREATE TABLE plan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    precio_mensual DECIMAL(10,2) DEFAULT 0.00,
    limite_usuarios INT DEFAULT 0,
    limite_pedidos_mes INT DEFAULT 0,
    limite_pedidos_simultaneos INT DEFAULT 5,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA TENANT
CREATE TABLE tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(150) NOT NULL,
    email_contacto VARCHAR(100),
    id_admin_mensajeria BIGINT,
    estado_id INT NOT NULL DEFAULT 1,
    plan_id INT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_conexion TIMESTAMP NULL,
    FOREIGN KEY (estado_id) REFERENCES estado(id),
	FOREIGN KEY (plan_id) REFERENCES plan(id)
);

-- VISTAS
-- Vista para información completa del tenant
CREATE VIEW v_tenant_info AS
SELECT 
    t.id,
    t.nombre_empresa,
    t.email_contacto,
    t.id_admin_mensajeria,
    e.nombre as estado,
    e.descripcion as estado_descripcion,
    p.nombre as plan,
    p.descripcion as plan_descripcion,
    p.precio_mensual,
    p.limite_usuarios,
    p.limite_pedidos_mes,
    p.limite_pedidos_simultaneos,
    t.fecha_creacion,
    t.fecha_ultima_conexion,
    CASE 
        WHEN t.fecha_ultima_conexion IS NULL THEN 'Nunca'
        WHEN DATEDIFF(NOW(), t.fecha_ultima_conexion) = 0 THEN 'Hoy'
        WHEN DATEDIFF(NOW(), t.fecha_ultima_conexion) = 1 THEN 'Ayer'
        ELSE CONCAT(DATEDIFF(NOW(), t.fecha_ultima_conexion), ' días atrás')
    END as ultima_conexion_texto
FROM tenant t
INNER JOIN estado e ON t.estado_id = e.id
INNER JOIN plan p ON t.plan_id = p.id;

-- STORED PROCEDURES
-- Procedimiento para actualizar última conexión del tenant
DELIMITER //
CREATE PROCEDURE sp_actualizar_ultima_conexion_tenant(
    IN p_tenant_id BIGINT
)
BEGIN
    UPDATE tenant 
    SET fecha_ultima_conexion = NOW() 
    WHERE id = p_tenant_id;
END //
DELIMITER ;

-- Procedimiento para obtener información completa del tenant
DELIMITER //
CREATE PROCEDURE sp_obtener_info_tenant(
    IN p_tenant_id BIGINT
)
BEGIN
    SELECT * FROM v_tenant_info WHERE id = p_tenant_id;
END //
DELIMITER ;

-- Procedimiento para validar límites del plan
DELIMITER //
CREATE PROCEDURE sp_validar_limites_plan(
    IN p_tenant_id BIGINT,
    IN p_tipo_limite VARCHAR(20), -- 'usuarios', 'pedidos_mes', 'pedidos_simultaneos'
    OUT p_limite_actual INT,
    OUT p_limite_maximo INT,
    OUT p_puede_crear BOOLEAN
)
BEGIN
    DECLARE v_usuarios_actuales INT DEFAULT 0;
    DECLARE v_pedidos_mes_actuales INT DEFAULT 0;
    DECLARE v_pedidos_simultaneos_actuales INT DEFAULT 0;
    
    -- Obtener límites del plan
    SELECT 
        p.limite_usuarios,
        p.limite_pedidos_mes,
        p.limite_pedidos_simultaneos
    INTO p_limite_maximo, @limite_pedidos_mes, @limite_pedidos_simultaneos
    FROM tenant t
    INNER JOIN plan p ON t.plan_id = p.id
    WHERE t.id = p_tenant_id;
    
    IF p_tipo_limite = 'usuarios' THEN
        -- Contar usuarios actuales (desde mensajeria schema)
        SET @sql = CONCAT('SELECT COUNT(*) INTO @count FROM mensajeria.usuario WHERE tenant_id = ', p_tenant_id, ' AND estado_id = 1');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET p_limite_actual = @count;
        SET p_puede_crear = (p_limite_actual < p_limite_maximo OR p_limite_maximo = 0);
        
    ELSEIF p_tipo_limite = 'pedidos_mes' THEN
        SET p_limite_maximo = @limite_pedidos_mes;
        -- Contar pedidos del mes actual
        SET @sql = CONCAT('SELECT COUNT(*) INTO @count FROM mensajeria.pedido WHERE tenant_id = ', p_tenant_id, ' AND MONTH(fecha_creacion) = MONTH(NOW()) AND YEAR(fecha_creacion) = YEAR(NOW())');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET p_limite_actual = @count;
        SET p_puede_crear = (p_limite_actual < p_limite_maximo OR p_limite_maximo = 0);
        
    ELSEIF p_tipo_limite = 'pedidos_simultaneos' THEN
        SET p_limite_maximo = @limite_pedidos_simultaneos;
        -- Contar pedidos activos (estados 1-4 asumiendo que son activos)
        SET @sql = CONCAT('SELECT COUNT(*) INTO @count FROM mensajeria.pedido WHERE tenant_id = ', p_tenant_id, ' AND estado_id IN (1,2,3,4)');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET p_limite_actual = @count;
        SET p_puede_crear = (p_limite_actual < p_limite_maximo OR p_limite_maximo = 0);
    END IF;
    
END //
DELIMITER ;

-- DATOS INICIALES
-- Estados para tenant
INSERT INTO estado (nombre, descripcion) VALUES 
('ACTIVO', 'Tenant activo y funcional'),
('INACTIVO', 'Tenant temporalmente inactivo'),
('SUSPENDIDO', 'Tenant suspendido por incumplimiento'),
('BLOQUEADO', 'Tenant bloqueado permanentemente');

-- Planes básicos
INSERT INTO plan (nombre, descripcion, precio_mensual, limite_usuarios, limite_pedidos_mes, limite_pedidos_simultaneos) VALUES 
('BASICO', 'Plan básico para pequeñas empresas', 50000, 5, 100, 10),
('PROFESIONAL', 'Plan profesional para empresas medianas', 120000, 15, 500, 25),
('EMPRESARIAL', 'Plan empresarial para grandes empresas', 250000, 50, 2000, 50),
('ILIMITADO', 'Plan sin restricciones', 500000, 0, 0, 0);

-- Tenants de ejemplo
INSERT INTO tenant (nombre_empresa, email_contacto, id_admin_mensajeria, estado_id, plan_id, fecha_ultima_conexion) VALUES
('Mensajería Rápida S.A.S', 'contacto@mensajeriarapida.com', 1, 1, 1, NOW() - INTERVAL 1 DAY), -- ACTIVO, BÁSICO
('Express Delivery LTDA', 'info@expressdelivery.com', 2, 1, 2, NOW() - INTERVAL 2 DAY), -- ACTIVO, PROFESIONAL
('Domicilios Norte', 'contacto@domiciliosnorte.com', 3, 2, 3, NULL), -- INACTIVO, EMPRESARIAL
('MegaCourier S.A', 'soporte@megacourier.com', NULL, 3, 4, NOW() - INTERVAL 5 DAY), -- SUSPENDIDO, ILIMITADO
('Eco Envíos SAS', 'admin@ecoenvios.com', NULL, 4, 2, NOW() - INTERVAL 10 DAY); -- BLOQUEADO, PROFESIONAL
