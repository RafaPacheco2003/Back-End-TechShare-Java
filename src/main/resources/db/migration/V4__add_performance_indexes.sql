-- V4__add_performance_indexes.sql
-- Añade índices para optimizar las consultas más frecuentes del sistema
-- Fecha: 9 de Octubre 2025
-- Versión: 2.0 - Implementación condicional para evitar duplicados

-- ============================================================================
-- FUNCIÓN AUXILIAR PARA CREAR ÍNDICES CONDICIONALMENTE
-- ============================================================================

-- Procedimiento para crear índices solo si no existen
DELIMITER $$

DROP PROCEDURE IF EXISTS CreateIndexIfNotExists$$
CREATE PROCEDURE CreateIndexIfNotExists(IN table_name VARCHAR(100), IN index_name VARCHAR(100), IN index_definition TEXT)
BEGIN
    DECLARE index_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO index_count
    FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE table_schema = DATABASE() 
    AND table_name = table_name 
    AND index_name = index_name;
    
    IF index_count = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', index_name, ' ', index_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- ============================================================================
-- ÍNDICES PARA TABLA MATERIALS
-- ============================================================================

-- Índice para búsquedas por nombre (frecuente en filtros y búsquedas)
CALL CreateIndexIfNotExists('materials', 'idx_materials_name', 'ON materials(name)');

-- Índice para relación con subcategorías (optimiza JOIN FETCH)
CALL CreateIndexIfNotExists('materials', 'idx_materials_subcategory', 'ON materials(subCategory_id)');

-- Índice compuesto para búsquedas con filtros múltiples
CALL CreateIndexIfNotExists('materials', 'idx_materials_stock_subcategory', 'ON materials(stock, subCategory_id)');


-- ============================================================================
-- ÍNDICES PARA TABLA BORROW (Préstamos)
-- ============================================================================

-- Índice para búsquedas por usuario (getAllBorrowsByUserId)
CALL CreateIndexIfNotExists('borrow', 'idx_borrow_usuario', 'ON borrow(usuario_id)');

-- Índice para filtros por estado (status IN (...))
CALL CreateIndexIfNotExists('borrow', 'idx_borrow_status', 'ON borrow(status)');

-- Índice para ordenamiento y filtros por fecha
CALL CreateIndexIfNotExists('borrow', 'idx_borrow_date', 'ON borrow(date)');

-- Índice compuesto para consultas frecuentes (usuario + estado)
CALL CreateIndexIfNotExists('borrow', 'idx_borrow_usuario_status', 'ON borrow(usuario_id, status)');


-- ============================================================================
-- ÍNDICES PARA TABLA MOVEMENTS (Movimientos)
-- ============================================================================

-- Índice para relación con materials (optimiza JOIN FETCH)
CALL CreateIndexIfNotExists('movements', 'idx_movements_material', 'ON movements(materials_id)');

-- Índice para búsquedas por usuario
CALL CreateIndexIfNotExists('movements', 'idx_movements_usuario', 'ON movements(usuario_id)');

-- Índice para filtros y ordenamiento por fecha (getMovementsByDate)
CALL CreateIndexIfNotExists('movements', 'idx_movements_date', 'ON movements(date)');

-- Índice para filtros por tipo de movimiento (getMovementsByType)
CALL CreateIndexIfNotExists('movements', 'idx_movements_type', 'ON movements(move_type)');

-- Índice compuesto para consultas frecuentes (material + fecha)
CALL CreateIndexIfNotExists('movements', 'idx_movements_material_date', 'ON movements(materials_id, date)');


-- ============================================================================
-- ÍNDICES PARA TABLA DETAILS_BORROW
-- ============================================================================

-- Índice para relación con borrow (optimiza JOIN FETCH)
CALL CreateIndexIfNotExists('details_borrow', 'idx_details_borrow', 'ON details_borrow(borrow_id)');

-- Índice para relación con materials
CALL CreateIndexIfNotExists('details_borrow', 'idx_details_material', 'ON details_borrow(materials_id)');


-- ============================================================================
-- ÍNDICES PARA TABLA USUARIO
-- ============================================================================

-- Índice para búsquedas por email (login, autenticación)
-- Ya existe como UNIQUE, pero lo mencionamos para documentación
-- CREATE INDEX idx_usuario_email ON usuario(email); -- No necesario, ya es UNIQUE

-- Índice para búsquedas por username
-- CREATE INDEX idx_usuario_username ON usuario(user_name); -- No necesario, ya es UNIQUE

-- Índice para filtros por estado de habilitación
CALL CreateIndexIfNotExists('usuario', 'idx_usuario_enabled', 'ON usuario(is_enabled)');


-- ============================================================================
-- ÍNDICES PARA TABLAS DE RELACIÓN (Many-to-Many)
-- ============================================================================

-- Índice para usuario_role (optimiza búsquedas de roles por usuario)
CALL CreateIndexIfNotExists('usuario_role', 'idx_usuario_role_usuario', 'ON usuario_role(usuario_id)');
CALL CreateIndexIfNotExists('usuario_role', 'idx_usuario_role_role', 'ON usuario_role(role_id)');

-- Índice para role_materials (optimiza permisos por rol)
CALL CreateIndexIfNotExists('role_materials', 'idx_role_materials_role', 'ON role_materials(role_id)');
CALL CreateIndexIfNotExists('role_materials', 'idx_role_materials_material', 'ON role_materials(materials_id)');


-- ============================================================================
-- ÍNDICES PARA SUBCATEGORIES Y CATEGORIES
-- ============================================================================

-- Índice para relación subcategory -> category (optimiza JOIN)
CALL CreateIndexIfNotExists('subCategories', 'idx_subcategories_category', 'ON subCategories(category_id)');

-- Índice para búsquedas por nombre de categoría
CALL CreateIndexIfNotExists('categories', 'idx_categories_name', 'ON categories(name)');

-- Índice para búsquedas por nombre de subcategoría
CALL CreateIndexIfNotExists('subCategories', 'idx_subcategories_name', 'ON subCategories(name)');

-- ============================================================================
-- LIMPIEZA DEL PROCEDIMIENTO AUXILIAR
-- ============================================================================

-- Eliminamos el procedimiento auxiliar para mantener la base limpia
DROP PROCEDURE IF EXISTS CreateIndexIfNotExists;


-- ============================================================================
-- NOTAS DE PERFORMANCE
-- ============================================================================
-- 1. Los índices en columnas UNIQUE (email, user_name) ya existen implícitamente
-- 2. Los índices en PRIMARY KEY ya existen implícitamente
-- 3. Los índices compuestos están ordenados por selectividad (más selectivo primero)
-- 4. Los índices en FOREIGN KEY mejoran significativamente los JOIN FETCH
-- 5. Estos índices benefician especialmente las consultas *Optimized en los repositorios
