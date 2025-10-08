# 🔐 Guía de Seguridad y Configuración

## ⚠️ ACCIÓN INMEDIATA REQUERIDA

### 1. **Proteger Credenciales de Email**

**PROBLEMA CRÍTICO:** Tus credenciales de Gmail están expuestas en `application.properties`:
```properties
spring.mail.username=rodrigorafaelchipacheco@gmail.com
spring.mail.password=rzlgjdhkkrflvuus
```

**SOLUCIÓN:**

#### Paso 1: Crear archivo `.env` (NO subir a Git)

```bash
# Crear archivo .env en la raíz del proyecto
cp .env.example .env
```

Editar `.env` con tus valores reales:
```env
MAIL_USERNAME=rodrigorafaelchipacheco@gmail.com
MAIL_PASSWORD=rzlgjdhkkrflvuus
JWT_SECRET=genera_una_clave_segura_de_al_menos_32_caracteres_aqui
```

#### Paso 2: Modificar `application.properties`

Reemplazar las credenciales hardcodeadas por variables de entorno:

```properties
# Configuración de Email (USAR VARIABLES DE ENTORNO)
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Paso 3: Actualizar `docker-compose.yml`

Agregar las variables de entorno al servicio backend:

```yaml
backend:
  environment:
    SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/techmate_inventory
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: admin1
    MAIL_HOST: smtp.gmail.com
    MAIL_PORT: 587
    MAIL_USERNAME: ${MAIL_USERNAME}
    MAIL_PASSWORD: ${MAIL_PASSWORD}
    JWT_SECRET: ${JWT_SECRET}
```

#### Paso 4: Verificar que `.env` está en `.gitignore`

```bash
# Verificar que .env no se suba a Git
git status

# Si aparece .env en la lista, agregarlo a .gitignore
echo ".env" >> .gitignore
```

#### Paso 5: **ELIMINAR credenciales del historial de Git** (IMPORTANTE)

```bash
# Si ya subiste las credenciales a Git, debes limpiar el historial
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch Back-End-TechShare-Java/src/main/resources/application.properties" \
  --prune-empty --tag-name-filter cat -- --all

# Forzar push (CUIDADO: esto reescribe el historial)
git push origin --force --all
```

#### Paso 6: **Rotar las credenciales comprometidas**

1. **Gmail:** Eliminar la contraseña de aplicación actual y generar una nueva:
   - Ir a https://myaccount.google.com/apppasswords
   - Eliminar la contraseña expuesta
   - Generar una nueva
   - Actualizar en tu archivo `.env`

2. **JWT Secret:** Generar una nueva clave segura:
   ```bash
   # Generar clave aleatoria de 256 bits
   openssl rand -base64 32
   ```

---

## 🛡️ **Mejores Prácticas de Seguridad**

### 1. **Variables de Entorno (OBLIGATORIO)**

**NUNCA** hardcodear:
- ❌ Contraseñas
- ❌ API Keys
- ❌ Secrets JWT
- ❌ Credenciales de base de datos
- ❌ Tokens de servicios externos

**SIEMPRE** usar:
- ✅ Variables de entorno (`.env`)
- ✅ Servicios de gestión de secretos (AWS Secrets Manager, Azure Key Vault)
- ✅ Archivos de configuración externos (no en Git)

### 2. **Validación de Inputs (IMPLEMENTADO)**

Ya tienes `EnhancedImageValidationStrategy` que valida:
- ✅ Tamaño máximo (10 MB)
- ✅ Tipos MIME permitidos
- ✅ Extensiones válidas
- ✅ Path traversal attacks

**Usar en tus servicios:**
```java
@Autowired
private EnhancedImageValidationStrategy imageValidator;

public MaterialsDTO createMaterials(MaterialsDTO dto, MultipartFile image) {
    // Validar antes de guardar
    imageValidator.validate(image);
    
    // Continuar con el guardado...
}
```

### 3. **CORS (YA CONFIGURADO)**

Tu `WebSecurityConfig` ya tiene CORS configurado correctamente:
- ✅ Permite localhost:3000 (frontend)
- ✅ Permite localhost:8080 (backend)
- ✅ Expone Authorization header
- ✅ Permite credentials

**Para producción:**
```java
// Cambiar a dominio real
configuration.setAllowedOrigins(Arrays.asList(
    "https://tu-dominio-produccion.com"
));
```

### 4. **JWT Tokens**

**Configuración actual:**
- ✅ Expiración: 10 días (`ACCESS_TOKEN_VALIDITY_SECONDS`)
- ✅ Firma HMAC256
- ⚠️ Secret hardcodeado (CAMBIAR A VARIABLE DE ENTORNO)

**Mejoras recomendadas:**
```java
// TokenUtils.java - Usar variable de entorno
@Value("${jwt.secret}")
private String jwtSecret;

private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}
```

### 5. **Rate Limiting (PENDIENTE)**

Proteger contra ataques de fuerza bruta:
```java
// Implementar con Bucket4j (ver MEJORAS_RECOMENDADAS.md)
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    // Limitar a 100 requests por minuto por IP
}
```

### 6. **HTTPS (PRODUCCIÓN)**

**Desarrollo:** HTTP está bien
**Producción:** OBLIGATORIO usar HTTPS

```properties
# application-prod.properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

### 7. **Headers de Seguridad**

Agregar headers HTTP de seguridad:
```java
// WebSecurityConfig.java
http
    .headers(headers -> headers
        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
        .frameOptions(frame -> frame.deny())
        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
    );
```

---

## 🔍 **Checklist de Seguridad**

Antes de desplegar a producción:

### Configuración:
- [ ] Credenciales en variables de entorno (NO en código)
- [ ] `.env` en `.gitignore`
- [ ] Secrets rotados si fueron comprometidos
- [ ] JWT secret de al menos 256 bits
- [ ] HTTPS habilitado
- [ ] CORS configurado para dominio de producción

### Validación:
- [ ] Validación de tamaño de archivos (✅ HECHO)
- [ ] Validación de tipos MIME (✅ HECHO)
- [ ] Sanitización de nombres de archivo (✅ HECHO)
- [ ] Validación de inputs en todos los endpoints
- [ ] Rate limiting implementado
- [ ] Protección contra SQL injection (✅ JPA/Hibernate)

### Logging y Monitoreo:
- [ ] Logs estructurados (JSON)
- [ ] No loggear información sensible
- [ ] Monitoreo de intentos de login fallidos
- [ ] Alertas de actividad sospechosa
- [ ] Health checks configurados

### Base de Datos:
- [ ] Usuario con permisos mínimos (no root)
- [ ] Backups automáticos configurados
- [ ] Conexión encriptada (SSL/TLS)
- [ ] Soft deletes en lugar de DELETE físico

### Docker:
- [ ] Volúmenes persistentes para datos (✅ HECHO)
- [ ] Imágenes base verificadas
- [ ] Usuario no-root en contenedores
- [ ] Secrets via Docker secrets (no env vars)
- [ ] Health checks en contenedores (✅ HECHO para DB)

---

## 🚨 **Incidentes de Seguridad**

### Si detectas una brecha de seguridad:

1. **Rotación inmediata de credenciales:**
   ```bash
   # Cambiar TODAS las contraseñas y secrets
   # Revocar tokens activos
   # Regenerar JWT secret
   ```

2. **Revisar logs:**
   ```bash
   docker compose logs -f backend | grep "401\|403\|500"
   ```

3. **Notificar al equipo**

4. **Documentar el incidente**

---

## 📚 **Recursos Adicionales**

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Docker Security](https://docs.docker.com/engine/security/)

---

**Última actualización:** 2 de octubre de 2025  
**Prioridad:** 🔴 CRÍTICA - Implementar AHORA
