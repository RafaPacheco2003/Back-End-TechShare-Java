# Usar una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado al contenedor
COPY target/techmate-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la aplicación (8080 por defecto en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
