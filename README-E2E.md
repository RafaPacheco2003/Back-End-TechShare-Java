# Pruebas E2E y credenciales

Este archivo describe cómo ejecutar pruebas end-to-end localmente y las credenciales de prueba incluidas por Flyway.

Credenciales incluidas:

- Admin:
  - Email: admin@system.com
  - Password: password

- Test user (E2E):
  - Email: tester@system.com
  - Password: test1234

Notas:
- El seed del admin está en `V1__init.sql`.
- El seed del usuario tester está en `V3__seed_e2e_user.sql` y usa un hash bcrypt.

Usar el script de prueba PowerShell:

1. Asegúrate de tener los contenedores levantados (docker compose up -d)
2. Ejecuta desde el directorio del backend:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File "./scripts/test-login.ps1" -BaseUrl "http://localhost:8080" -Email "tester@system.com" -Password "test1234"
```

El script hará login, imprimirá el token, decodificará claims y llamará `/user/info`.

Si quieres usar el admin, reemplaza `-Email` y `-Password` por las credenciales del admin.
