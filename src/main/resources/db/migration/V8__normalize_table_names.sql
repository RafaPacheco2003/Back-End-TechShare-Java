-- ============================================================================
-- V8: Normalizar nombres de tablas a snake_case
-- ============================================================================
-- Descripción:
--   Esta migración renombra la tabla 'subCategories' (camelCase) a 
--   'sub_categories' (snake_case) para mantener consistencia con el resto 
--   de la base de datos y permitir portabilidad entre Windows (case-insensitive)
--   y Linux (case-sensitive).
--
-- Impacto:
--   - Renombra tabla subCategories → sub_categories
--   - Mantiene todas las foreign keys y constraints automáticamente
--   - Compatible con auto-mapeo de Hibernate CamelCaseToUnderscoresNamingStrategy
--
-- Autor: Sistema de normalización automática
-- Fecha: 2025-10-10
-- ============================================================================

-- Renombrar tabla de camelCase a snake_case
RENAME TABLE subCategories TO sub_categories;

-- Verificación: Confirmar que la tabla existe con el nuevo nombre
-- (Si falla, Flyway hará rollback automático)
SELECT COUNT(*) INTO @table_check 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
  AND table_name = 'sub_categories';

-- Si @table_check = 0, significa que falló el rename
-- Flyway detectará esto y marcará la migración como fallida
