-- Insertar el rol ROOT si no existe
INSERT INTO role (role_id, nombre) 
VALUES (1, 'ROOT')
ON DUPLICATE KEY UPDATE nombre = 'ROOT';

-- Insertar el usuario root si no existe
INSERT INTO usuario (id, user_name, first_name, last_name, email, password)
VALUES (1, 'root', 'Root', 'User', 'root@techmate.com', '');

-- Asignar el rol ROOT al usuario root en la tabla intermedia
INSERT INTO usuario_role (id, usuario_id, role_id)
VALUES (1, 1, 1);
