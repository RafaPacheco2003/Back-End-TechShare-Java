-- V9: Añadir columna birth_date a la tabla usuario si no existe
-- (Archivado) Esta copia fue retirada de `src/main/resources/db/migration` para evitar que Flyway la ejecute automáticamente.
-- Contenido original:

-- Evita usar 'ADD COLUMN IF NOT EXISTS' que falla en algunas versiones de MySQL
-- Usar PREPARE/EXECUTE con consulta a INFORMATION_SCHEMA para mantener compatibilidad

SET @col_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'usuario'
    AND column_name = 'birth_date'
);

SET @sql_stmt = IF(@col_exists = 0,
  'ALTER TABLE usuario ADD COLUMN birth_date DATE NULL',
  'SELECT 1'
);

PREPARE stmt FROM @sql_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Nota: Flyway ejecuta este script en contexto SQL normal; evitar delimitadores / bloques PL/SQL.
