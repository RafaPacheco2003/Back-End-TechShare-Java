# Swagger / OpenAPI

Esta guía explica cómo acceder a la documentación OpenAPI (Swagger UI) de la API y cómo usar el esquema Bearer para probar rutas protegidas.

## Dependencias
Se añadió `springdoc-openapi-starter-webmvc-ui` al `pom.xml`. Esto expone la UI de Swagger automáticamente.

## URLs útiles (modo desarrollo)
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Esquema OpenAPI: http://localhost:8080/v3/api-docs

## Probar rutas protegidas
1. Haz login en http://localhost:8080/login (POST JSON {"email":"...","password":"..."}).
2. Obtén el token JWT desde la cabecera `Authorization` o desde el  body (`token`).
3. En Swagger UI, pulsa el botón `Authorize` (arriba a la derecha) y pega:

```
Bearer <TU_TOKEN_AQUI>
```

4. Ahora las llamadas en la UI incluirán la cabecera Authorization: Bearer <token>.

## Notas de seguridad
- En producción siempre usar HTTPS.
- Evitar exponer secretos en el repositorio; establecer `JWT_SECRET` y demás variables por entorno.
- Considerar refresh tokens y mecanismos de revocación si la app requiere logout seguro.

## Variables de entorno recomendadas
- JWT_SECRET: secreto HMAC para firmar los JWT. Debe ser una cadena larga y guardada fuera del repositorio.
- JWT_EXPIRATION_SECONDS: (opcional) tiempo de expiración del access token en segundos. Si no se define, por defecto será 3600 (1 hora).

Ejemplo en desarrollo (Windows PowerShell):

```powershell
$env:JWT_SECRET = 'tu_secreto_largo_aqui'
$env:JWT_EXPIRATION_SECONDS = '3600'
``` 

En Docker Compose, define estas variables en el servicio `backend` (o en un archivo .env) para que la aplicación las lea al arrancar.

## Levantar con Docker Compose (rápido)

1. Copia el archivo de ejemplo `.env.example` a `.env` en la raíz del repo y rellena los valores (NO subas `.env`):

```powershell
Set-Location -Path "G:\\TechShare"
cp .env.example .env
# Edita .env con tu editor favorito y cambia JWT_SECRET, MAIL_*, etc.
```

2. Arranca la base de datos y el backend (recomiendo --build la primera vez):

```powershell
docker compose up --build -d db backend
```

3. Comprueba el estado y logs:

```powershell
docker compose ps
docker compose logs backend --tail 200 -f
```

4. Abre la UI de Swagger en el navegador:

```
http://localhost:8080/swagger-ui/index.html
```

Notas importantes:
- Si ves en los logs un error como `Missing JWT_SECRET environment variable`, rellena `JWT_SECRET` en tu `.env` (o exporta la variable en el host) y vuelve a levantar el servicio.
- El archivo `.env.example` contiene las variables necesarias para desarrollo; úsalo como plantilla.
