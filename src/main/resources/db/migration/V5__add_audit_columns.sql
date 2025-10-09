-- V5: Agregar columnas de auditoría a tablas principales
-- Registra quién creó/modificó cada registro y cuándo

-- Procedimiento auxiliar para agregar columnas condicionalmente
DELIMITER $$

DROP PROCEDURE IF EXISTS AddColumnIfNotExists$$
CREATE PROCEDURE AddColumnIfNotExists(IN table_name VARCHAR(100), IN column_name VARCHAR(100), IN column_definition TEXT)
BEGIN
    DECLARE column_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO column_count
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE table_schema = DATABASE() 
    AND table_name = table_name 
    AND column_name = column_name;
    
    IF column_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', table_name, ' ADD COLUMN ', column_name, ' ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- Tabla materials
CALL AddColumnIfNotExists('materials', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('materials', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('materials', 'created_by', 'VARCHAR(100)');
CALL AddColumnIfNotExists('materials', 'updated_by', 'VARCHAR(100)');

-- Tabla borrow
CALL AddColumnIfNotExists('borrow', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('borrow', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('borrow', 'created_by', 'VARCHAR(100)');
CALL AddColumnIfNotExists('borrow', 'updated_by', 'VARCHAR(100)');

-- Tabla movements
CALL AddColumnIfNotExists('movements', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('movements', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('movements', 'created_by', 'VARCHAR(100)');
CALL AddColumnIfNotExists('movements', 'updated_by', 'VARCHAR(100)');

-- Tabla categories
CALL AddColumnIfNotExists('categories', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('categories', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('categories', 'created_by', 'VARCHAR(100)');
CALL AddColumnIfNotExists('categories', 'updated_by', 'VARCHAR(100)');

-- Tabla subcategories
CALL AddColumnIfNotExists('subcategories', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('subcategories', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('subcategories', 'created_by', 'VARCHAR(100)');
CALL AddColumnIfNotExists('subcategories', 'updated_by', 'VARCHAR(100)');

-- Tabla usuario
CALL AddColumnIfNotExists('usuario', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('usuario', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('usuario', 'created_by', 'VARCHAR(100) DEFAULT ''system''');
CALL AddColumnIfNotExists('usuario', 'updated_by', 'VARCHAR(100)');

-- Tabla role
CALL AddColumnIfNotExists('role', 'created_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('role', 'updated_at', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColumnIfNotExists('role', 'created_by', 'VARCHAR(100) DEFAULT ''system''');
CALL AddColumnIfNotExists('role', 'updated_by', 'VARCHAR(100)');
