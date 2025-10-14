-- V1__seed_roles.sql
-- Seed inicial de roles para TechShare
-- Esta migración es idempotente: sólo inserta si no existen los roles con esos ids.

INSERT INTO role (role_id, nombre, created_at)
SELECT 1, 'admin', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_id = 1);

INSERT INTO role (role_id, nombre, created_at)
SELECT 2, 'user', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_id = 2);

INSERT INTO role (role_id, nombre, created_at)
SELECT 3, 'librarian', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_id = 3);

-- Nota: no creamos el admin por defecto aquí con contraseña en claro. Usa el script
-- scripts/create_admin.ps1 para crear un admin con contraseña segura (hash bcrypt)
-- o integra la creación del admin en tu pipeline usando un secreto del secret manager.
