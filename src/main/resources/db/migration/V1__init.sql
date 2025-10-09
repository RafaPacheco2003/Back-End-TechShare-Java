-- V1__init.sql - migración inicial para Flyway
-- Crea las tablas principales, índices y datos iniciales (sin triggers ni vistas)

-- Tabla roles (coincide con la entidad Role)
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE subCategories (
    subCategory_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_path VARCHAR(255),
    category_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    user_name VARCHAR(100) UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE materials (
    materials_id INT AUTO_INCREMENT PRIMARY KEY,
    image_path VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE,
    stock INT NOT NULL DEFAULT 0,
    borrowable_stock INT NOT NULL DEFAULT 0,
    subCategory_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CHECK (stock >= 0),
    CHECK (borrowable_stock >= 0 AND borrowable_stock <= stock),
    FOREIGN KEY (subCategory_id) REFERENCES subCategories(subCategory_id) ON DELETE SET NULL
);

CREATE TABLE borrow (
    borrow_id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE,
    fecha DATE,
    start_date DATE,
    end_date DATE,
    return_date DATE NULL,
    status VARCHAR(50),
    usuario_id INT,
    admin_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CHECK (amount IS NULL OR amount >= 0),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (admin_id) REFERENCES usuario(id)
);

CREATE TABLE movements (
    movements_id INT AUTO_INCREMENT PRIMARY KEY,
    comment TEXT,
    date DATETIME,
    move_type VARCHAR(50),
    quantity INT NOT NULL,
    materials_id INT,
    usuario_id INT,
    borrow_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (quantity != 0),
    FOREIGN KEY (materials_id) REFERENCES materials(materials_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (borrow_id) REFERENCES borrow(borrow_id)
);

CREATE TABLE verification_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date DATETIME NOT NULL,
    user_id INT NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE usuario_role (
    usuario_id INT,
    role_id INT,
    PRIMARY KEY (usuario_id, role_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
);

CREATE TABLE role_materials (
    role_materials_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT,
    materials_id INT,
    UNIQUE KEY unique_role_material (role_id, materials_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (materials_id) REFERENCES materials(materials_id) ON DELETE CASCADE
);

CREATE TABLE details_borrow (
    details_borrow_id INT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DOUBLE,
    total_price DOUBLE,
    borrow_id INT,
    materials_id INT,
    CHECK (quantity > 0),
    CHECK (unit_price IS NULL OR unit_price >= 0),
    CHECK (total_price IS NULL OR total_price >= 0),
    UNIQUE KEY unique_borrow_material (borrow_id, materials_id),
    FOREIGN KEY (borrow_id) REFERENCES borrow(borrow_id) ON DELETE CASCADE,
    FOREIGN KEY (materials_id) REFERENCES materials(materials_id)
);

-- Índices
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_enabled ON usuario(is_enabled);
CREATE INDEX idx_subcategories_category ON subCategories(category_id);
CREATE INDEX idx_materials_subcategory ON materials(subCategory_id);
CREATE INDEX idx_materials_name ON materials(name);
CREATE INDEX idx_materials_stock ON materials(stock);
CREATE INDEX idx_borrow_usuario ON borrow(usuario_id);
CREATE INDEX idx_borrow_admin ON borrow(admin_id);
CREATE INDEX idx_borrow_status ON borrow(status);
CREATE INDEX idx_borrow_dates ON borrow(start_date, end_date);
CREATE INDEX idx_borrow_fecha ON borrow(fecha);
CREATE INDEX idx_movements_material ON movements(materials_id);
CREATE INDEX idx_movements_usuario ON movements(usuario_id);
CREATE INDEX idx_movements_borrow ON movements(borrow_id);
CREATE INDEX idx_movements_date ON movements(date);
CREATE INDEX idx_movements_type ON movements(move_type);
CREATE INDEX idx_verification_token_token ON verification_token(token);
CREATE INDEX idx_verification_token_user ON verification_token(user_id);
CREATE INDEX idx_verification_token_expiry ON verification_token(expiry_date);
CREATE INDEX idx_usuario_role_role ON usuario_role(role_id);
CREATE INDEX idx_role_materials_material ON role_materials(materials_id);
CREATE INDEX idx_details_borrow_borrow ON details_borrow(borrow_id);
CREATE INDEX idx_details_borrow_material ON details_borrow(materials_id);

-- Datos iniciales (roles, categorías, subcategorías, usuario admin)
-- Datos iniciales (roles, categorías, subcategorías, usuario admin)
INSERT INTO role (nombre) VALUES ('admin'), ('user'), ('librarian');
INSERT INTO categories (name) VALUES ('Electrónicos'), ('Herramientas'), ('Libros'), ('Deportes');
INSERT INTO subCategories (name, category_id) VALUES ('Laptops', 1), ('Tablets', 1), ('Herramientas Manuales', 2), ('Herramientas Eléctricas', 2), ('Ficción', 3), ('Técnicos', 3), ('Fútbol', 4), ('Baloncesto', 4);
-- Nota: la contraseña debe ser un hash compatible con la implementación; aquí usamos un hash de ejemplo
INSERT INTO usuario (email, first_name, last_name, user_name, password, is_enabled) VALUES ('admin@system.com', 'Admin', 'System', 'admin', '$2b$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE);

INSERT INTO usuario_role (usuario_id, role_id) SELECT u.id, r.role_id FROM usuario u, role r WHERE u.email = 'admin@system.com' AND r.nombre = 'admin';

-- Nota: los triggers y vistas se pueden agregar en migraciones posteriores (V2__triggers.sql, V3__views.sql)
