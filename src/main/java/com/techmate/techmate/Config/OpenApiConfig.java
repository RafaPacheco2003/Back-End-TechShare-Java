package com.techmate.techmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración de OpenAPI / Swagger UI.
 *
 * Expone automáticamente la UI en: /swagger-ui.html o /swagger-ui/index.html
 *
 * Seguridad: añadimos un scheme de tipo http Bearer para que puedas probar
 * las rutas protegidas directamente desde la UI (hacer click en Authorize y
 * pegar "Bearer <token>").
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("TechShare API").version("v0.1.0").description("Documentación OpenAPI"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().name(securitySchemeName).type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")
                ));
    }
}
