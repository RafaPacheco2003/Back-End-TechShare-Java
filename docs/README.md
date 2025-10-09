# 📚 Documentación TechShare Backend# 📘 TechShare Backend - Guía Completa



**Índice maestro de toda la documentación técnica del proyecto****Versión:** 0.2.0  

**Spring Boot:** 3.4.1  

---**Java:** 17  

**Estado:** ✅ Production Ready (9.5/10)

## 🗂️ Organización de la Documentación

---

### 📖 **Documentación Principal**

- **[README.md](../README.md)** - Guía principal del proyecto## 🚀 Inicio Rápido

- **[MEJORAS_IMPLEMENTADAS_10-10.md](../MEJORAS_IMPLEMENTADAS_10-10.md)** - Lista de mejoras aplicadas

### Requisitos

### 🚀 **Guías de Inicio**- Java 17+

- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Setup y desarrollo local- MySQL 8.0+

- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Deployment en producción- Maven 3.8+

- **[README-SWAGGER.md](README-SWAGGER.md)** - Configuración de API docs

### Ejecución Local

### 🛠️ **Guías Técnicas**```bash

- **[CI_CD_IMPLEMENTACION.md](CI_CD_IMPLEMENTACION.md)** - Pipeline de CI/CD# 1. Clonar repositorio

- **[EXPLICACION_AUDITABLEENTITY.md](EXPLICACION_AUDITABLEENTITY.md)** - Sistema de auditoríagit clone https://github.com/DARKTOTEM2703/Back-End-TechShare-Java.git

- **[README-E2E.md](README-E2E.md)** - Tests end-to-endcd Back-End-TechShare-Java



### 📝 **Historial y Cambios**# 2. Configurar base de datos (crear BD 'techshare')

- **[CHANGELOG.md](CHANGELOG.md)** - Registro de cambios por versiónmysql -u root -p -e "CREATE DATABASE techshare;"

- **[CONSOLIDACION_DOCS.md](CONSOLIDACION_DOCS.md)** - Consolidación de documentación

# 3. Configurar variables de entorno

### 🎯 **Mejoras Futuras**export DB_URL=jdbc:mysql://localhost:3306/techshare

- **[MEJORAS_FUTURAS.md](MEJORAS_FUTURAS.md)** - Roadmap y próximas featuresexport DB_USERNAME=root

- **[MEJORAS_PRIORIDAD_MEDIA.md](MEJORAS_PRIORIDAD_MEDIA.md)** - Mejoras priorizadasexport DB_PASSWORD=tu_password

export JWT_SECRET=tu-secreto-seguro-aqui

---

# 4. Ejecutar aplicación

## 🏃‍♂️ **Para Empezar Rápidamente**./mvnw spring-boot:run

```

### Si eres **nuevo en el proyecto**:

1. Lee el **[README principal](../README.md)**### Con Docker

2. Sigue la **[guía de desarrollo](DEVELOPMENT.md)**```bash

3. Revisa la **[documentación de API](README-SWAGGER.md)**docker-compose up -d

```

### Si vas a **deployar en producción**:

1. Lee la **[guía de deployment](DEPLOYMENT.md)****Acceso:**

2. Configura **[CI/CD](CI_CD_IMPLEMENTACION.md)**- API: http://localhost:8080

3. Revisa el **[changelog](CHANGELOG.md)**- Swagger UI: http://localhost:8080/swagger-ui.html (si SWAGGER_ENABLED=true)

- Health Check: http://localhost:8080/actuator/health

### Si vas a **contribuir código**:

1. Sigue las **[buenas prácticas](DEVELOPMENT.md#estándares-de-código)**---

2. Entiende el **[sistema de auditoría](EXPLICACION_AUDITABLEENTITY.md)**

3. Ejecuta **[tests E2E](README-E2E.md)**## 📋 Características Principales



---### ✅ Funcionalidades

- 🔐 Autenticación JWT con roles (ADMIN, USER)

## 📋 **Documentos por Tipo**- 📦 Gestión de materiales e inventario

- 🔄 Sistema de préstamos (borrow)

### 🔧 **Setup y Configuración**- 📊 Movimientos de stock (entrada/salida)

| Documento | Propósito | Audiencia |- 🏷️ Categorías y subcategorías

|-----------|-----------|-----------|- 📧 Notificaciones por email

| [DEVELOPMENT.md](DEVELOPMENT.md) | Setup completo para desarrollo | Desarrolladores |- 🖼️ Gestión de imágenes

| [DEPLOYMENT.md](DEPLOYMENT.md) | Deployment en producción | DevOps/SysAdmin |

| [README-SWAGGER.md](README-SWAGGER.md) | Configuración de OpenAPI | Desarrolladores API |### 🎯 Optimizaciones Implementadas

- ✅ **Queries JOIN FETCH** - Eliminación de N+1 queries

### 🏗️ **Arquitectura y Diseño**- ✅ **HikariCP Pool** - Conexiones optimizadas

| Documento | Propósito | Audiencia |- ✅ **DTOs + Mappers** - Separación de capas

|-----------|-----------|-----------|- ✅ **GlobalExceptionHandler** - Manejo centralizado de errores

| [EXPLICACION_AUDITABLEENTITY.md](EXPLICACION_AUDITABLEENTITY.md) | Sistema de auditoría | Desarrolladores |- ✅ **22 Índices de BD** - Performance mejorada

| [CI_CD_IMPLEMENTACION.md](CI_CD_IMPLEMENTACION.md) | Pipeline automatizado | DevOps |- ✅ **Caché Caffeine** - Cache en memoria para categorías/roles

- ✅ **Actuator** - Monitoreo y health checks

### 🧪 **Testing y QA**

| Documento | Propósito | Audiencia |---

|-----------|-----------|-----------|

| [README-E2E.md](README-E2E.md) | Tests end-to-end | QA/Testers |## 🔧 Configuración



### 📊 **Gestión de Proyecto**### Variables de Entorno

| Documento | Propósito | Audiencia |

|-----------|-----------|-----------|#### Base de Datos

| [CHANGELOG.md](CHANGELOG.md) | Historial de cambios | Todo el equipo |```bash

| [MEJORAS_FUTURAS.md](MEJORAS_FUTURAS.md) | Roadmap del proyecto | Product Owner |DB_URL=jdbc:mysql://localhost:3306/techshare

| [MEJORAS_PRIORIDAD_MEDIA.md](MEJORAS_PRIORIDAD_MEDIA.md) | Backlog priorizado | Equipo técnico |DB_USERNAME=root

DB_PASSWORD=password

---```



## 📞 **Obtener Ayuda**#### Seguridad

```bash

### Para dudas sobre:JWT_SECRET=tu-secreto-jwt-minimo-256-bits

- **Setup inicial:** [DEVELOPMENT.md](DEVELOPMENT.md)```

- **Deployment:** [DEPLOYMENT.md](DEPLOYMENT.md)  

- **API usage:** [README-SWAGGER.md](README-SWAGGER.md)#### Features

- **Tests:** [README-E2E.md](README-E2E.md)```bash

SWAGGER_ENABLED=true              # Habilitar Swagger UI

### Si no encuentras respuesta:SERVER_URL=http://localhost:8080  # URL base del servidor

1. **Busca en issues** de GitHubSTORAGE_LOCATION=uploaded-images  # Directorio de imágenes

2. **Revisa documentos relacionados**```

3. **Crea issue** con template apropiado

#### HikariCP (Opcional)

---```bash

HIKARI_MAX_POOL_SIZE=10           # Default: 10

**Documentación actualizada:** 9 de octubre de 2025  HIKARI_MINIMUM_IDLE=5             # Default: 5

**Versión del proyecto:** 10/10 Perfect  HIKARI_CONNECTION_TIMEOUT=30000   # Default: 30000ms

**Estado:** ✅ Producción ReadyHIKARI_IDLE_TIMEOUT=600000        # Default: 600000ms
HIKARI_MAX_LIFETIME=1800000       # Default: 1800000ms
```

### application.properties
```properties
# Ver archivo completo en:
# src/main/resources/application.properties
```

---

## 🗄️ Base de Datos

### Migraciones Flyway
```
V1__init.sql                           # Tablas iniciales
V2__add_id_to_usuario_role.sql        # ID en tabla intermedia
V3__seed_e2e_user.sql                 # Usuario de pruebas
V4__add_performance_indexes.sql       # 22 índices de performance
```

### Ejecutar manualmente
```bash
./mvnw flyway:migrate
```

---

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests (71)
./mvnw clean test

# Tests específicos
./mvnw test -Dtest=MaterialsControllerTest
```

### Cobertura
- **Total:** 71/71 tests ✅
- **Unitarios:** Controllers, Services, Mappers
- **Integración:** Repositorios, Security, API completa
- **E2E:** Flujos completos de usuario

---

## 📚 API Documentation

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Requiere `SWAGGER_ENABLED=true`

### Endpoints Principales

#### Autenticación
```
POST /auth/register    # Registro de usuario
POST /auth/login       # Login (retorna JWT)
```

#### Materiales (requiere ADMIN)
```
GET    /admin/materials/all         # Listar (paginado)
GET    /admin/materials/{id}        # Obtener por ID
POST   /admin/materials/create      # Crear
PUT    /admin/materials/update/{id} # Actualizar
DELETE /admin/materials/delete/{id} # Eliminar
```

#### Préstamos
```
POST /borrow/create              # Crear préstamo (USER)
GET  /borrow/user/borrows        # Mis préstamos (USER)
GET  /admin/borrow/all           # Todos los préstamos (ADMIN)
PUT  /admin/borrow/update/{id}   # Cambiar estado (ADMIN)
```

#### Movimientos (ADMIN)
```
GET  /admin/movement/all              # Todos
GET  /admin/movement/{id}             # Por ID
POST /admin/movement/create           # Crear
GET  /admin/movement/filterByDate     # Filtrar por fecha
```

#### Actuator (público)
```
GET /actuator/health    # Estado de salud
GET /actuator/info      # Información de la app
```

---

## 🏗️ Arquitectura

### Estructura del Proyecto
```
src/main/java/com/techmate/techmate/
├── Controller/          # REST Controllers
│   ├── MaterialsController.java
│   ├── BorrowController.java
│   ├── MovementsController.java
│   └── User/           # Endpoints para usuarios normales
├── Service/            # Lógica de negocio
│   ├── impl/          # Implementaciones
│   └── mapper/        # DTOs mappers
├── repository/         # JPA Repositories
├── entity/            # Entidades JPA
├── dto/               # Data Transfer Objects
├── security/          # JWT, Auth, Config
├── exception/         # Exception Handlers
└── config/            # Configuraciones
    ├── OpenApiConfig.java
    ├── CacheConfig.java
    └── WebSecurityConfig.java
```

### Patrones Implementados
- **Repository Pattern** - Acceso a datos
- **DTO Pattern** - Transferencia de datos
- **Mapper Pattern** - Conversión Entity ↔ DTO
- **Service Layer** - Lógica de negocio
- **Exception Handler** - Manejo centralizado de errores

---

## 🔐 Seguridad

### JWT Authentication
- Algoritmo: HS256
- Expiración: Configurable
- Claims: id, email, roles

### Roles
- **ADMIN** - Acceso completo (CRUD de todo)
- **USER** - Solo lectura y préstamos propios

### CORS
- Configurado globalmente en `WebSecurityConfig`
- Permite orígenes específicos en producción

### Swagger Security
- Deshabilitado por defecto en producción
- Requiere `SWAGGER_ENABLED=true` para activar

---

## 📊 Monitoreo

### Actuator Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Info de la aplicación
curl http://localhost:8080/actuator/info

# Métricas (requiere auth)
curl http://localhost:8080/actuator/metrics
```

### Logs
- SLF4J + Logback
- Nivel configurable por paquete
- Hibernate statistics habilitadas (detectar N+1)

---

## 🐳 Docker

### Docker Compose
```bash
# Iniciar todo (MySQL + Backend)
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Detener
docker-compose down
```

### Health Checks
- MySQL: `mysqladmin ping`
- Backend: `/actuator/health`
- Retries automáticos

---

## 🔄 Desarrollo

Ver [DEVELOPMENT.md](DEVELOPMENT.md) para:
- Guía de contribución
- Estándares de código
- Workflow de Git
- Debugging tips

---

## 🚀 Deployment

Ver [DEPLOYMENT.md](DEPLOYMENT.md) para:
- Despliegue en producción
- Configuración de servidor
- SSL/HTTPS setup
- Backup y recuperación

---

## 📝 Changelog

Ver [CHANGELOG.md](CHANGELOG.md) para historial completo de cambios.

---

## 🤝 Contribuidores

- **DARKTOTEM2703** - Desarrollo principal
- Equipo TechShare

---

## 📄 Licencia

[Especificar licencia]

---

## 📞 Soporte

- Issues: [GitHub Issues](https://github.com/DARKTOTEM2703/Back-End-TechShare-Java/issues)
- Email: [tu-email@ejemplo.com]

---

**Última actualización:** 8 de Octubre 2025  
**Versión:** 0.2.0
