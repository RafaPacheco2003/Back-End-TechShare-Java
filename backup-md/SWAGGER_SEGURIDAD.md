# 🔒 Configuración de Seguridad de Swagger

## ⚠️ IMPORTANTE: Swagger solo en desarrollo

Por razones de seguridad, **Swagger está deshabilitado por defecto** en este proyecto.

### 🏗️ Desarrollo Local (Docker Compose)

Swagger **está habilitado automáticamente** cuando ejecutas:

```bash
docker compose up
```

La variable `SWAGGER_ENABLED=true` está configurada por defecto en `docker-compose.yml`.

**Acceso a Swagger UI:**
- URL: http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/api-docs

### 🚀 Producción

**Swagger estará DESHABILITADO** automáticamente si no defines la variable de entorno.

Para producción, asegúrate de:

```bash
# NO definir SWAGGER_ENABLED, o explícitamente:
export SWAGGER_ENABLED=false
```

### 🛠️ Habilitar/Deshabilitar Manualmente

#### Opción 1: Variable de entorno

```bash
# Habilitar
export SWAGGER_ENABLED=true

# Deshabilitar (o simplemente no definir la variable)
export SWAGGER_ENABLED=false
```

#### Opción 2: En docker-compose.yml

```yaml
environment:
  SWAGGER_ENABLED: "true"   # Habilitar
  # o
  SWAGGER_ENABLED: "false"  # Deshabilitar
```

#### Opción 3: En archivo .env

```env
# Desarrollo
SWAGGER_ENABLED=true

# Producción
SWAGGER_ENABLED=false
```

### 🔐 ¿Por qué está deshabilitado en producción?

**Riesgos de seguridad si Swagger está público:**
- ❌ Expone toda la estructura de tu API
- ❌ Revela nombres de endpoints, parámetros y modelos
- ❌ Muestra esquemas de base de datos
- ❌ Ayuda a atacantes a encontrar vulnerabilidades
- ❌ Viola estándares de seguridad (PCI-DSS, SOC2, etc.)

**Beneficios de deshabilitarlo en producción:**
- ✅ Máxima seguridad (Zero attack surface)
- ✅ Mejor performance (sin overhead de generación de docs)
- ✅ Cumplimiento de estándares de seguridad
- ✅ Previene Information Disclosure

### 📚 Alternativas para documentación en producción

Si necesitas compartir documentación con clientes/partners:

1. **Documentación estática**: Genera HTML/PDF desde Swagger y publícalo por separado
2. **Postman Collections**: Exporta la API y compártela vía Postman
3. **Portal de desarrolladores**: Usa plataformas como ReadMe.io, Stoplight, GitBook
4. **Swagger privado**: Requiere autenticación (cambia `permitAll()` por `hasRole("ADMIN")`)

### 🔍 Verificar estado actual

Para verificar si Swagger está habilitado:

```bash
# En Docker
docker exec techshare-backend env | grep SWAGGER_ENABLED

# Intenta acceder a la UI
curl -I http://localhost:8080/swagger-ui/index.html
# Si devuelve 401 Unauthorized → Swagger deshabilitado ✅
# Si devuelve 200 OK → Swagger habilitado (solo en dev)
```

### 🚨 Checklist antes de producción

- [ ] `SWAGGER_ENABLED` no está definida o es `false`
- [ ] Verificar que `/swagger-ui/index.html` devuelve 401
- [ ] Verificar que `/api-docs` devuelve 401
- [ ] Revisar logs de startup (no debe aparecer "springdoc-openapi" iniciando)
- [ ] Test de seguridad: intentar acceder desde navegador (debe fallar)

---

**Última actualización**: 3 de octubre de 2025  
**Configurado por**: GitHub Copilot  
**Versión de seguridad**: v1.0
