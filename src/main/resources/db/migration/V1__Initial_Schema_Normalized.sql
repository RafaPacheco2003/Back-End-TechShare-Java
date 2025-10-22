-- =============================================================================
-- V1__Initial_Schema_Normalized.sql
-- Migración INICIAL que crea todo en snake_case desde cero
-- Esta migración REEMPLAZA el esquema caótico anterior
-- =============================================================================

-- PASO 1: Eliminar todas las tablas viejas (con nombres en camelCase)
-- Esto es seguro porque estamos en una migración Flyway controlada

DROP TABLE IF EXISTS usuario_role;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS sub_categories;
DROP TABLE IF EXISTS subCategories;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS usuario;

-- PASO 2: Crear tablas en snake_case (FORMA CORRECTA)

-- Tabla: roles
CREATE TABLE roles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: users (antes "usuario")
CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: categories
CREATE TABLE categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: sub_categories (ANTES "subCategories" - AHORA NORMALIZADA)
CREATE TABLE sub_categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sub_category_id INT NOT NULL AUTO_INCREMENT UNIQUE,
    category_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_name_category (name, category_id),
    INDEX idx_category (category_id),
    INDEX idx_sub_category_id (sub_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: materials
CREATE TABLE materials (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    sub_category_id INT NOT NULL,
    image_path VARCHAR(255),
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(sub_category_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_sub_category (sub_category_id),
    INDEX idx_created_by (created_by),
    FULLTEXT INDEX ft_search (name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: usuario_role (relación muchos-a-muchos: ESTRUCTURA CORRECTA)
CREATE TABLE usuario_role (
    id INT NOT NULL AUTO_INCREMENT UNIQUE,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_id (id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: reviews
CREATE TABLE reviews (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    material_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_material_user (material_id, user_id),
    INDEX idx_material (material_id),
    INDEX idx_user (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: favorites
CREATE TABLE favorites (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    material_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_material (user_id, material_id),
    INDEX idx_user (user_id),
    INDEX idx_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PASO 3: Insertar datos iniciales de ejemplo

-- Roles por defecto
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrador del sistema'),
('USER', 'Usuario regular'),
('VENDOR', 'Vendedor de materiales');

-- Admin usuario por defecto (contraseña: admin123 hasheada)
INSERT INTO users (username, email, password, first_name, last_name, is_active) VALUES 
('admin', 'admin@techshare.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DIP5Ctkyy6WO0/LewKpDt3xbS3QTZG', 'Admin', 'User', TRUE);

-- Admin role para el usuario admin
INSERT INTO usuario_role (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Categorías iniciales
INSERT INTO categories (name, description) VALUES 
('Electrónica', 'Componentes y dispositivos electrónicos'),
('Hardware', 'Componentes de computadora'),
('Accesorios', 'Accesorios varios'),
('Software', 'Licencias y software');

-- Subcategorías iniciales
INSERT INTO sub_categories (category_id, name, description) VALUES 
(1, 'Microcontroladores', 'Arduino, ESP32, STM32'),
(1, 'Sensores', 'Sensores de temperatura, humedad, distancia'),
(2, 'Procesadores', 'CPUs y GPUs'),
(2, 'Memoria', 'RAM, SSD, HDD'),
(3, 'Cables', 'Cables USB, HDMI, Ethernet'),
(3, 'Conectores', 'Conectores y adaptadores');

-- PASO 4: Verificaciones finales
-- Asegurar que todo se creó correctamente en snake_case
-- Esto se valida automáticamente por Hibernate en el siguiente deploy
