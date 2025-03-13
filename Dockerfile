# Usar una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine as builder

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los archivos de la aplicación (incluyendo el pom.xml)
COPY . .

# Dar permisos de ejecución al archivo mvnw
RUN chmod +x mvnw

# Compilar la aplicación y generar el .jar
RUN ./mvnw clean package -DskipTests

# Segunda etapa: imagen ligera para ejecutar la aplicación
FROM eclipse-temurin:17-jre-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado en la etapa anterior
COPY --from=builder /app/target/techmate-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la aplicación (8080 por defecto en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]