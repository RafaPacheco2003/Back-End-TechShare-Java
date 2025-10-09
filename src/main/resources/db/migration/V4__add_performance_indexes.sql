-- V4__add_performance_indexes.sql
-- Añade índices para optimizar las consultas más frecuentes del sistema
-- Fecha: 8 de Octubre 2025
-- Referencia: MEJORAS_IMPLEMENTADAS_OCT_08_2025.md

-- ============================================================================
-- ÍNDICES PARA TABLA MATERIALS
-- ============================================================================

-- Índice para búsquedas por nombre (frecuente en filtros y búsquedas)
CREATE INDEX idx_materials_name ON materials(name);

-- Índice para relación con subcategorías (optimiza JOIN FETCH)
CREATE INDEX idx_materials_subcategory ON materials(subCategory_id);

-- Índice compuesto para búsquedas con filtros múltiples
CREATE INDEX idx_materials_stock_subcategory ON materials(stock, subCategory_id);


-- ============================================================================
-- ÍNDICES PARA TABLA BORROW (Préstamos)
-- ============================================================================

-- Índice para búsquedas por usuario (getAllBorrowsByUserId)
CREATE INDEX idx_borrow_usuario ON borrow(usuario_id);

-- Índice para filtros por estado (status IN (...))
CREATE INDEX idx_borrow_status ON borrow(status);

-- Índice para ordenamiento y filtros por fecha
CREATE INDEX idx_borrow_date ON borrow(date);

-- Índice compuesto para consultas frecuentes (usuario + estado)
CREATE INDEX idx_borrow_usuario_status ON borrow(usuario_id, status);


-- ============================================================================
-- ÍNDICES PARA TABLA MOVEMENTS (Movimientos)
-- ============================================================================

-- Índice para relación con materials (optimiza JOIN FETCH)
CREATE INDEX idx_movements_material ON movements(materials_id);

-- Índice para búsquedas por usuario
CREATE INDEX idx_movements_usuario ON movements(usuario_id);

-- Índice para filtros y ordenamiento por fecha (getMovementsByDate)
CREATE INDEX idx_movements_date ON movements(date);

-- Índice para filtros por tipo de movimiento (getMovementsByType)
CREATE INDEX idx_movements_type ON movements(move_type);

-- Índice compuesto para consultas frecuentes (material + fecha)
CREATE INDEX idx_movements_material_date ON movements(materials_id, date);


-- ============================================================================
-- ÍNDICES PARA TABLA DETAILS_BORROW
-- ============================================================================

-- Índice para relación con borrow (optimiza JOIN FETCH)
CREATE INDEX idx_details_borrow ON details_borrow(borrow_id);

-- Índice para relación con materials
CREATE INDEX idx_details_material ON details_borrow(materials_id);


-- ============================================================================
-- ÍNDICES PARA TABLA USUARIO
-- ============================================================================

-- Índice para búsquedas por email (login, autenticación)
-- Ya existe como UNIQUE, pero lo mencionamos para documentación
-- CREATE INDEX idx_usuario_email ON usuario(email); -- No necesario, ya es UNIQUE

-- Índice para búsquedas por username
-- CREATE INDEX idx_usuario_username ON usuario(user_name); -- No necesario, ya es UNIQUE

-- Índice para filtros por estado de habilitación
CREATE INDEX idx_usuario_enabled ON usuario(is_enabled);


-- ============================================================================
-- ÍNDICES PARA TABLAS DE RELACIÓN (Many-to-Many)
-- ============================================================================

-- Índice para usuario_role (optimiza búsquedas de roles por usuario)
CREATE INDEX idx_usuario_role_usuario ON usuario_role(usuario_id);
CREATE INDEX idx_usuario_role_role ON usuario_role(role_id);

-- Índice para role_materials (optimiza permisos por rol)
CREATE INDEX idx_role_materials_role ON role_materials(role_id);
CREATE INDEX idx_role_materials_material ON role_materials(materials_id);


-- ============================================================================
-- ÍNDICES PARA SUBCATEGORIES Y CATEGORIES
-- ============================================================================

-- Índice para relación subcategory -> category (optimiza JOIN)
CREATE INDEX idx_subcategories_category ON subCategories(category_id);

-- Índice para búsquedas por nombre de categoría
CREATE INDEX idx_categories_name ON categories(name);

-- Índice para búsquedas por nombre de subcategoría
CREATE INDEX idx_subcategories_name ON subCategories(name);


-- ============================================================================
-- NOTAS DE PERFORMANCE
-- ============================================================================
-- 1. Los índices en columnas UNIQUE (email, user_name) ya existen implícitamente
-- 2. Los índices en PRIMARY KEY ya existen implícitamente
-- 3. Los índices compuestos están ordenados por selectividad (más selectivo primero)
-- 4. Los índices en FOREIGN KEY mejoran significativamente los JOIN FETCH
-- 5. Estos índices benefician especialmente las consultas *Optimized en los repositorios
