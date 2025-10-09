-- V6__rename_image_columns.sql
-- Renombra columnas "imagePath" a "image_path" si existen (para instalaciones previas)
-- Esta migración es idempotente y segura: verifica si la columna antigua existe y la nueva no, y luego renombra.

DELIMITER $$

DROP PROCEDURE IF EXISTS RenameImageColumnsIfExists$$
CREATE PROCEDURE RenameImageColumnsIfExists()
BEGIN
    DECLARE col_exists INT DEFAULT 0;
    DECLARE new_col_exists INT DEFAULT 0;

    -- categories
    SELECT COUNT(*) INTO col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND COLUMN_NAME = 'imagePath';
    SELECT COUNT(*) INTO new_col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND COLUMN_NAME = 'image_path';
    IF col_exists = 1 AND new_col_exists = 0 THEN
        ALTER TABLE categories CHANGE COLUMN `imagePath` `image_path` VARCHAR(255);
    END IF;

    -- subCategories
    SELECT COUNT(*) INTO col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'subCategories' AND COLUMN_NAME = 'imagePath';
    SELECT COUNT(*) INTO new_col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'subCategories' AND COLUMN_NAME = 'image_path';
    IF col_exists = 1 AND new_col_exists = 0 THEN
        ALTER TABLE subCategories CHANGE COLUMN `imagePath` `image_path` VARCHAR(255);
    END IF;

    -- materials
    SELECT COUNT(*) INTO col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'materials' AND COLUMN_NAME = 'imagePath';
    SELECT COUNT(*) INTO new_col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'materials' AND COLUMN_NAME = 'image_path';
    IF col_exists = 1 AND new_col_exists = 0 THEN
        ALTER TABLE materials CHANGE COLUMN `imagePath` `image_path` VARCHAR(255);
    END IF;
END$$

DELIMITER ;

CALL RenameImageColumnsIfExists();

DROP PROCEDURE IF EXISTS RenameImageColumnsIfExists;
