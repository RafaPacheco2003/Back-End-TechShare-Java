package com.techmate.techmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Propiedades de configuración personalizadas de la aplicación.
 * Centraliza todas las propiedades custom para facilitar el mantenimiento.
 * 
 * @see application.properties
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Configuración de CORS
     */
    @NotNull
    private Cors cors = new Cors();

    /**
     * Configuración de verificación de email
     */
    @NotNull
    private Verification verification = new Verification();

    /**
     * Configuración de almacenamiento de imágenes
     */
    @NotNull
    private Storage storage = new Storage();

    /**
     * URL base del servidor (usado para construir URLs de imágenes)
     */
    @NotBlank
    private String serverUrl = "http://localhost:8080";

    @Data
    public static class Cors {
        /**
         * Lista de orígenes permitidos para CORS.
         * Ejemplo: http://localhost:3000,http://localhost:3001
         */
        private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:3001");
    }

    @Data
    public static class Verification {
        /**
         * URL base para la verificación de email.
         * Se concatena con el token para formar la URL completa.
         */
        @NotBlank
        private String url = "http://localhost:8080/verify?token=";
    }

    @Data
    public static class Storage {
        /**
         * Directorio donde se almacenan las imágenes subidas.
         * Ruta relativa al directorio de ejecución de la aplicación.
         */
        @NotBlank
        private String location = "uploaded-images";
    }
}
