-- V2__add_id_to_usuario_role.sql
-- V2__add_id_to_usuario_role.sql (safe swap)
-- En lugar de DROP PRIMARY KEY (lo que falla si existen constraints), creamos una tabla nueva
-- con la columna id AUTO_INCREMENT, copiamos los datos, eliminamos la tabla antigua y renombramos.

CREATE TABLE usuario_role_new (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  role_id INT NOT NULL,
  KEY idx_usuario_role_role (role_id),
  CONSTRAINT usuario_role_new_ibfk_1 FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT usuario_role_new_ibfk_2 FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE,
  UNIQUE KEY unique_usuario_role (usuario_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiar datos existentes (respetando el par usuario_id, role_id)
INSERT INTO usuario_role_new (usuario_id, role_id)
  SELECT usuario_id, role_id FROM usuario_role;

-- Eliminar la tabla antigua y renombrar la nueva
DROP TABLE usuario_role;
RENAME TABLE usuario_role_new TO usuario_role;
-- No need to add the unique key: it is already defined in the new table definition.
