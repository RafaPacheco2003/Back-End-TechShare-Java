# Usar una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Instalar cliente mysql para poder verificar disponibilidad de la base de datos
RUN apk add --no-cache mariadb-client netcat-openbsd

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado al contenedor
COPY target/techmate-0.0.1-SNAPSHOT.jar app.jar

# Copiar el script de espera
COPY wait-for-db.sh /usr/local/bin/wait-for-db.sh
RUN chmod +x /usr/local/bin/wait-for-db.sh

# Exponer el puerto en el que corre la aplicación (8080 por defecto en Spring Boot)
EXPOSE 8080

# Usar el script de espera como entrypoint para asegurar que MySQL esté listo
ENTRYPOINT ["/usr/local/bin/wait-for-db.sh"]
