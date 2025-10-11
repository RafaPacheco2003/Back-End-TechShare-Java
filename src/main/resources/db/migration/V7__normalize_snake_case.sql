-- ============================================
-- V7: Normalizar nombres a snake_case puro
-- Para compatibilidad total con Linux (case-sensitive)
-- ============================================

-- 1. Renombrar subCategory_id -> sub_category_id en materials (si existe)
SET @dbname = DATABASE();
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE table_schema = @dbname AND table_name = 'materials' AND column_name = 'subCategory_id') > 0,
  'ALTER TABLE materials CHANGE COLUMN `subCategory_id` `sub_category_id` INT(11)',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- 2. Renombrar imagePath -> image_path en materials (si existe)
SET @preparedStatement2 = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE table_schema = @dbname AND table_name = 'materials' AND column_name = 'imagePath') > 0,
  'ALTER TABLE materials CHANGE COLUMN `imagePath` `image_path` VARCHAR(255)',
  'SELECT 1'
));
PREPARE alterIfExists2 FROM @preparedStatement2;
EXECUTE alterIfExists2;
DEALLOCATE PREPARE alterIfExists2;

-- 3. Renombrar subCategory_id -> sub_category_id en subCategories (si existe)
SET @preparedStatement3 = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE table_schema = @dbname AND table_name = 'subCategories' AND column_name = 'subCategory_id') > 0,
  'ALTER TABLE subCategories CHANGE COLUMN `subCategory_id` `sub_category_id` INT(11) AUTO_INCREMENT',
  'SELECT 1'
));
PREPARE alterIfExists3 FROM @preparedStatement3;
EXECUTE alterIfExists3;
DEALLOCATE PREPARE alterIfExists3;

-- 4. Renombrar imagePath -> image_path en subCategories (si existe)
SET @preparedStatement4 = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE table_schema = @dbname AND table_name = 'subCategories' AND column_name = 'imagePath') > 0,
  'ALTER TABLE subCategories CHANGE COLUMN `imagePath` `image_path` VARCHAR(255)',
  'SELECT 1'
));
PREPARE alterIfExists4 FROM @preparedStatement4;
EXECUTE alterIfExists4;
DEALLOCATE PREPARE alterIfExists4;

-- 5. Renombrar imagePath -> image_path en categories (si existe)
SET @preparedStatement5 = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE table_schema = @dbname AND table_name = 'categories' AND column_name = 'imagePath') > 0,
  'ALTER TABLE categories CHANGE COLUMN `imagePath` `image_path` VARCHAR(255)',
  'SELECT 1'
));
PREPARE alterIfExists5 FROM @preparedStatement5;
EXECUTE alterIfExists5;
DEALLOCATE PREPARE alterIfExists5;

-- Nota: Todas las columnas ahora usan snake_case puro
-- Compatible con Linux case-sensitive filesystems
