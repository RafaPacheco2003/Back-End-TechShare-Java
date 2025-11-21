# 📘 TechShare Backend - Guía Completa

**Versión:** 0.2.0  
**Spring Boot:** 3.4.1  
**Java:** 17  
**Estado:** ✅ Production Ready (9.5/10)

---

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### Ejecución Local
```bash
# 1. Clonar repositorio
git clone https://github.com/DARKTOTEM2703/Back-End-TechShare-Java.git
cd Back-End-TechShare-Java

# 2. Configurar base de datos (crear BD 'techshare')
mysql -u root -p -e "CREATE DATABASE techshare;"

# 3. Configurar variables de entorno
export DB_URL=jdbc:mysql://localhost:3306/techshare
export DB_USERNAME=root
export DB_PASSWORD=tu_password
export JWT_SECRET=tu-secreto-seguro-aqui

# 4. Ejecutar aplicación
./mvnw spring-boot:run
```

### Con Docker
```bash
docker-compose up -d
```

**Acceso:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html (si SWAGGER_ENABLED=true)
- Health Check: http://localhost:8080/actuator/health

---

## 📋 Características Principales

### ✅ Funcionalidades
- 🔐 Autenticación JWT con roles (ADMIN, USER)
- 📦 Gestión de materiales e inventario
- 🔄 Sistema de préstamos (borrow)
- 📊 Movimientos de stock (entrada/salida)
- 🏷️ Categorías y subcategorías
- 📧 Notificaciones por email
- 🖼️ Gestión de imágenes

### 🎯 Optimizaciones Implementadas
- ✅ **Queries JOIN FETCH** - Eliminación de N+1 queries
- ✅ **HikariCP Pool** - Conexiones optimizadas
- ✅ **DTOs + Mappers** - Separación de capas
- ✅ **GlobalExceptionHandler** - Manejo centralizado de errores
- ✅ **22 Índices de BD** - Performance mejorada
- ✅ **Caché Caffeine** - Cache en memoria para categorías/roles
- ✅ **Actuator** - Monitoreo y health checks

---

## 🔧 Configuración

### Variables de Entorno

#### Base de Datos
```bash
DB_URL=jdbc:mysql://localhost:3306/techshare
DB_USERNAME=root
DB_PASSWORD=password
```

#### Seguridad
```bash
JWT_SECRET=tu-secreto-jwt-minimo-256-bits
```

#### Features
```bash
SWAGGER_ENABLED=true              # Habilitar Swagger UI
SERVER_URL=http://localhost:8080  # URL base del servidor
STORAGE_LOCATION=uploaded-images  # Directorio de imágenes
```

#### HikariCP (Opcional)
```bash
HIKARI_MAX_POOL_SIZE=10           # Default: 10
HIKARI_MINIMUM_IDLE=5             # Default: 5
HIKARI_CONNECTION_TIMEOUT=30000   # Default: 30000ms
HIKARI_IDLE_TIMEOUT=600000        # Default: 600000ms
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
