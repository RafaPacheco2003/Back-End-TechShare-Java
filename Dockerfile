# ============================================
# STAGE 1: BUILD
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Establecer directorio de trabajo
WORKDIR /build

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (capa cacheada)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (saltar tests para build más rápido)
RUN mvn clean package -DskipTests -B

# ============================================
# STAGE 2: RUNTIME
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="TechShare Team"
LABEL version="0.5.0"
LABEL description="TechShare Backend - Spring Boot 3.4.1"

# Instalar utilidades necesarias
RUN apk add --no-cache \
    mariadb-client \
    netcat-openbsd \
    curl \
    && rm -rf /var/cache/apk/*

# Crear usuario no-root para seguridad
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde el stage de build
COPY --from=builder /build/target/*.jar app.jar

# Copiar el script de espera
COPY wait-for-db.sh /usr/local/bin/wait-for-db.sh
RUN chmod +x /usr/local/bin/wait-for-db.sh

# Crear directorios necesarios
RUN mkdir -p /app/uploaded-images /app/logs && \
    chmod -R 755 /app/uploaded-images && \
    chmod -R 755 /app/logs && \
    chown -R spring:spring /app && \
    chmod -R u+rwx,g+rx,o+rx /app/logs /app/uploaded-images

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check - Temporalmente deshabilitado para debug
# HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#     CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto
# Agregado: -XX:+PrintFlagsFinal para ver flags reales
# Agregado: -XX:+PrintGCDetails para ver garbage collection
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintFlagsFinal"
ENV SPRING_PROFILES_ACTIVE=prod

# Entrypoint con configuración JVM
ENTRYPOINT ["/usr/local/bin/wait-for-db.sh"]
