# 🚀 Guía de Deployment - TechShare Backend

Instrucciones para desplegar la aplicación en diferentes entornos.

---

## 📋 Tabla de Contenidos

1. [Pre-requisitos](#pre-requisitos)
2. [Variables de Entorno](#variables-de-entorno)
3. [Deployment con Docker](#deployment-con-docker)
4. [Deployment Manual](#deployment-manual)
5. [Configuración de Base de Datos](#configuración-de-base-de-datos)
6. [SSL/HTTPS](#sslhttps)
7. [Nginx Reverse Proxy](#nginx-reverse-proxy)
8. [Monitoreo y Logging](#monitoreo-y-logging)
9. [Backup y Recuperación](#backup-y-recuperación)
10. [Troubleshooting](#troubleshooting)

---

## 📦 Pre-requisitos

### Servidor de Producción
- **OS**: Ubuntu 20.04+ / Debian 11+ / RHEL 8+
- **RAM**: Mínimo 2GB, recomendado 4GB+
- **CPU**: Mínimo 2 cores
- **Disco**: 20GB+ disponible
- **Java**: JDK 17+ instalado
- **MySQL**: 8.0+ (o compatible)
- **Docker**: 20.10+ (opcional, para deployment con Docker)

### Firewall Rules
```bash
# Permitir HTTP/HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Permitir SSH (si aplica)
sudo ufw allow 22/tcp

# Aplicar reglas
sudo ufw enable
```

---

## 🔐 Variables de Entorno

### Archivo `.env` de Producción

Crear archivo `.env` en el servidor:

```bash
# Base de Datos
DB_HOST=mysql-server.example.com
DB_PORT=3306
DB_NAME=techmate_prod
DB_USER=techmate_user
DB_PASSWORD=STRONG_PASSWORD_HERE

# JWT (cambiar por secreto único y fuerte)
JWT_SECRET=GENERATE_STRONG_SECRET_KEY_256_BITS_MIN
JWT_EXPIRATION=86400000

# Swagger (deshabilitar en producción)
SWAGGER_ENABLED=false

# HikariCP
HIKARI_MAX_POOL_SIZE=20
HIKARI_MINIMUM_IDLE=10
HIKARI_CONNECTION_TIMEOUT=30000
HIKARI_IDLE_TIMEOUT=600000
HIKARI_MAX_LIFETIME=1800000

# Actuator (restringir en producción)
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info
MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/actuator

# Logging
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_APP=INFO
LOGGING_FILE_NAME=/var/log/techmate/application.log

# CORS (ajustar al dominio de producción)
CORS_ALLOWED_ORIGINS=https://techshare.example.com,https://www.techshare.example.com

# Recursos de imagen
IMAGES_UPLOAD_PATH=/opt/techmate/uploaded-images
```

### Generar JWT Secret Seguro

```bash
# Generar secreto de 256 bits
openssl rand -base64 32
```

---

## 🐳 Deployment con Docker

### 1. Build de la Imagen

```bash
# Clonar repositorio
git clone <repository-url>
cd Back-End-TechShare-Java

# Build de imagen Docker
docker build -t techmate-backend:latest .

# Verificar imagen
docker images | grep techmate
```

### 2. Docker Compose (Producción)

Crear `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: techmate-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./db-init:/docker-entrypoint-initdb.d
    networks:
      - techmate-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    image: techmate-backend:latest
    container_name: techmate-backend
    restart: unless-stopped
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
      SWAGGER_ENABLED: false
      LOGGING_LEVEL_ROOT: WARN
      LOGGING_LEVEL_APP: INFO
    ports:
      - "8080:8080"
    volumes:
      - uploaded_images:/app/uploaded-images
      - logs:/var/log/techmate
    networks:
      - techmate-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

volumes:
  mysql_data:
  uploaded_images:
  logs:

networks:
  techmate-network:
    driver: bridge
```

### 3. Iniciar Servicios

```bash
# Cargar variables de entorno
export $(cat .env | xargs)

# Iniciar servicios
docker-compose -f docker-compose.prod.yml up -d

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f backend

# Verificar salud
curl http://localhost:8080/actuator/health
```

---

## 🛠️ Deployment Manual

### 1. Instalación de Java

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk -y

# Verificar
java -version
```

### 2. Build del Proyecto

```bash
# En máquina de desarrollo
git clone <repository-url>
cd Back-End-TechShare-Java

# Build con Maven
./mvnw clean package -DskipTests

# Copiar JAR al servidor
scp target/techmate-0.0.1-SNAPSHOT.jar user@server:/opt/techmate/
```

### 3. Configuración en Servidor

```bash
# Crear directorio de aplicación
sudo mkdir -p /opt/techmate
sudo mkdir -p /opt/techmate/uploaded-images
sudo mkdir -p /var/log/techmate

# Crear usuario de servicio
sudo useradd -r -s /bin/false techmate
sudo chown -R techmate:techmate /opt/techmate
sudo chown -R techmate:techmate /var/log/techmate

# Copiar JAR
sudo cp techmate-0.0.1-SNAPSHOT.jar /opt/techmate/app.jar

# Crear archivo de configuración
sudo nano /opt/techmate/application.properties
```

### 4. Systemd Service

Crear `/etc/systemd/system/techmate.service`:

```ini
[Unit]
Description=TechMate Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=techmate
Group=techmate
WorkingDirectory=/opt/techmate

EnvironmentFile=/opt/techmate/.env

ExecStart=/usr/bin/java \
    -Xms512m -Xmx1024m \
    -Dspring.config.location=/opt/techmate/application.properties \
    -jar /opt/techmate/app.jar

SuccessExitStatus=143
Restart=on-failure
RestartSec=10

StandardOutput=journal
StandardError=journal
SyslogIdentifier=techmate

[Install]
WantedBy=multi-user.target
```

### 5. Iniciar Servicio

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar servicio
sudo systemctl enable techmate

# Iniciar servicio
sudo systemctl start techmate

# Ver estado
sudo systemctl status techmate

# Ver logs
sudo journalctl -u techmate -f
```

---

## 🗄️ Configuración de Base de Datos

### MySQL Production Setup

```bash
# Conectar a MySQL
mysql -u root -p

# Crear base de datos y usuario
CREATE DATABASE techmate_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'techmate_user'@'%' IDENTIFIED BY 'STRONG_PASSWORD';
GRANT ALL PRIVILEGES ON techmate_prod.* TO 'techmate_user'@'%';
FLUSH PRIVILEGES;
```

### Ejecutar Migraciones Flyway

```bash
# Opción 1: Flyway se ejecuta automáticamente al iniciar la app
# Las migraciones en src/main/resources/db/migration se aplican automáticamente

# Opción 2: Ejecutar manualmente con Maven
./mvnw flyway:migrate \
  -Dflyway.url=jdbc:mysql://localhost:3306/techmate_prod \
  -Dflyway.user=techmate_user \
  -Dflyway.password=STRONG_PASSWORD
```

### Optimización de MySQL

```sql
-- my.cnf o my.ini
[mysqld]
# InnoDB settings
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# Connection pool
max_connections = 200
wait_timeout = 600

# Query cache (si aplica)
query_cache_type = 1
query_cache_size = 64M

# Character set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

---

## 🔒 SSL/HTTPS

### Opción 1: Let's Encrypt (Recomendado)

```bash
# Instalar Certbot
sudo apt install certbot -y

# Obtener certificado
sudo certbot certonly --standalone -d api.techshare.example.com

# Certificados en:
# /etc/letsencrypt/live/api.techshare.example.com/fullchain.pem
# /etc/letsencrypt/live/api.techshare.example.com/privkey.pem

# Auto-renovación
sudo certbot renew --dry-run
```

### Opción 2: Spring Boot con SSL

Configurar en `application.properties`:

```properties
# HTTPS
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=/opt/techmate/keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=techmate

# Redirigir HTTP a HTTPS
server.http.port=8080
```

---

## 🌐 Nginx Reverse Proxy

### Instalación

```bash
sudo apt update
sudo apt install nginx -y
```

### Configuración Nginx

Crear `/etc/nginx/sites-available/techmate`:

```nginx
upstream techmate_backend {
    server localhost:8080 fail_timeout=0;
}

server {
    listen 80;
    server_name api.techshare.example.com;

    # Redirigir a HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.techshare.example.com;

    # SSL Certificates
    ssl_certificate /etc/letsencrypt/live/api.techshare.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.techshare.example.com/privkey.pem;

    # SSL Configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Logs
    access_log /var/log/nginx/techmate-access.log;
    error_log /var/log/nginx/techmate-error.log;

    # Request limits
    client_max_body_size 20M;

    # Proxy headers
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;

    # Health check endpoint (sin autenticación)
    location /actuator/health {
        proxy_pass http://techmate_backend;
    }

    # API endpoints
    location /api/ {
        proxy_pass http://techmate_backend;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Swagger (solo si está habilitado)
    location /swagger-ui/ {
        proxy_pass http://techmate_backend;
    }

    location /v3/api-docs/ {
        proxy_pass http://techmate_backend;
    }

    # Static images
    location /uploaded-images/ {
        alias /opt/techmate/uploaded-images/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### Habilitar Sitio

```bash
# Crear symlink
sudo ln -s /etc/nginx/sites-available/techmate /etc/nginx/sites-enabled/

# Test configuración
sudo nginx -t

# Reiniciar Nginx
sudo systemctl restart nginx
```

---

## 📊 Monitoreo y Logging

### Spring Boot Actuator

Endpoints disponibles:
- **Health**: `https://api.techshare.example.com/actuator/health`
- **Info**: `https://api.techshare.example.com/actuator/info`
- **Metrics** (protegido): `/actuator/metrics`

### Configurar Logrotate

Crear `/etc/logrotate.d/techmate`:

```
/var/log/techmate/application.log {
    daily
    rotate 30
    compress
    delaycompress
    notifempty
    create 0640 techmate techmate
    sharedscripts
    postrotate
        systemctl reload techmate > /dev/null 2>&1 || true
    endscript
}
```

### Prometheus + Grafana (Opcional)

```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:latest
    volumes:
      - grafana_data:/var/lib/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin

volumes:
  prometheus_data:
  grafana_data:
```

---

## 💾 Backup y Recuperación

### Backup Automático de MySQL

Crear script `/opt/techmate/backup-db.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/opt/techmate/backups"
DATE=$(date +%Y%m%d_%H%M%S)
FILENAME="techmate_backup_$DATE.sql.gz"

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Backup de base de datos
mysqldump -u techmate_user -p'PASSWORD' techmate_prod | gzip > $BACKUP_DIR/$FILENAME

# Eliminar backups antiguos (mantener últimos 7 días)
find $BACKUP_DIR -name "techmate_backup_*.sql.gz" -mtime +7 -delete

echo "Backup completado: $FILENAME"
```

### Cron Job para Backups Diarios

```bash
# Editar crontab
sudo crontab -e

# Agregar backup diario a las 2 AM
0 2 * * * /opt/techmate/backup-db.sh >> /var/log/techmate/backup.log 2>&1
```

### Restauración de Backup

```bash
# Descomprimir y restaurar
gunzip -c /opt/techmate/backups/techmate_backup_20251008_020000.sql.gz | \
mysql -u techmate_user -p techmate_prod
```

---

## 🔧 Troubleshooting

### Error: "Connection refused" al iniciar

```bash
# Verificar que MySQL esté corriendo
sudo systemctl status mysql

# Verificar conectividad
mysql -h localhost -u techmate_user -p -e "SELECT 1"

# Verificar variables de entorno
systemctl show techmate | grep Environment
```

### Error: OutOfMemoryError

```bash
# Aumentar memoria en systemd service
ExecStart=/usr/bin/java -Xms1024m -Xmx2048m -jar /opt/techmate/app.jar

# Reiniciar
sudo systemctl daemon-reload
sudo systemctl restart techmate
```

### Error: Too many connections (MySQL)

```sql
-- Verificar conexiones actuales
SHOW PROCESSLIST;

-- Aumentar max_connections
SET GLOBAL max_connections = 300;

-- Ajustar HikariCP en .env
HIKARI_MAX_POOL_SIZE=30
```

### Verificar Logs

```bash
# Logs de aplicación
sudo journalctl -u techmate -f

# Logs de Nginx
sudo tail -f /var/log/nginx/techmate-error.log

# Logs de MySQL
sudo tail -f /var/log/mysql/error.log
```

---

## ✅ Checklist de Deployment

Antes de ir a producción:

- [ ] Variables de entorno configuradas correctamente
- [ ] JWT_SECRET generado (256 bits mínimo)
- [ ] SWAGGER_ENABLED=false en producción
- [ ] Base de datos MySQL configurada y optimizada
- [ ] Migraciones Flyway ejecutadas
- [ ] SSL/HTTPS configurado
- [ ] Nginx reverse proxy configurado
- [ ] Firewall rules aplicadas
- [ ] Systemd service configurado y habilitado
- [ ] Backups automáticos configurados
- [ ] Logs rotados correctamente
- [ ] Health checks funcionando
- [ ] Tests de carga ejecutados
- [ ] Plan de rollback definido

---

**Última actualización:** 8 de Octubre 2025
