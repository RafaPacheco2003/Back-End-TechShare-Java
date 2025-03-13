# Usar una imagen base con Java 17 y Maven
FROM maven:3.8.6-eclipse-temurin-17-alpine as builder

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar la caché de dependencias
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar todas las dependencias para aprovechar la caché de Docker
RUN mvn dependency:go-offline -B

# Copiar el resto del código fuente
COPY src ./src

# Compilar la aplicación y generar el .jar
RUN mvn clean package -DskipTests

# Segunda etapa: imagen ligera para ejecutar la aplicación
FROM eclipse-temurin:17-jre-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado en la etapa anterior
COPY --from=builder /app/target/techmate-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la aplicación (8080 por defecto en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]