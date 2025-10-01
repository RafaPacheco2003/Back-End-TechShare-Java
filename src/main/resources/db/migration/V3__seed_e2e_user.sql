-- V3__seed_e2e_user.sql - agrega un usuario de pruebas para e2e
-- Usuario: tester@system.com
-- Contraseña (plain): test1234
-- La contraseña se inserta como hash bcrypt (generado localmente)

INSERT INTO usuario (email, first_name, last_name, user_name, password, is_enabled)
VALUES ('tester@system.com', 'Test', 'User', 'tester', '$2a$10$7QJwGZzq8o0QGdZtH0Z1Je3zqJ8rD7qgkO9e6yXv0n5b6Aq9L8Y3K', TRUE);

INSERT INTO usuario_role (usuario_id, role_id)
SELECT u.id, r.role_id FROM usuario u, role r WHERE u.email = 'tester@system.com' AND r.nombre = 'user';
