package com.techmate.techmate.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    // Filtro que valida el JWT en cada petición entrante
    private final JWTAuthorizationFilter jWTAuthorizationFilter;

    // Servicio para cargar usuarios desde la base de datos (JPA)
    private final UserDetailsService userDetailsService;

    /**
     * SecurityFilterChain principal.
     * - CSRF deshabilitado porque usamos JWT (stateless)
     * - CORS configurado por bean separado
     * - Rutas públicas declaradas explícitamente
     * - /admin/** requiere rol ADMIN
     * - /api/** requiere autenticación
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authnManager) throws Exception {
        // Filtro de autenticación personalizado para el endpoint /login
    JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
    jwtAuthenticationFilter.setAuthenticationManager(authnManager);
    jwtAuthenticationFilter.setFilterProcessesUrl("/login");
    // Ensure the authentication filter only triggers on POST /login so OPTIONS preflight are not handled
    jwtAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));

    http
        .cors(Customizer.withDefaults()) // Habilita CORS y usa el CorsFilter registrado
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitado para APIs REST basadas en JWT
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
            // Allow preflight OPTIONS requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Endpoints públicos (login y registro)
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/verify").permitAll()
                        // Recursos estáticos y uploads
                        .requestMatchers("/admin/categories/images/**", "/admin/materials/images/**", "/admin/subcategories/images/**", "/uploaded-images/**").permitAll()
                        // Rutas de administración requieren rol ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // API general requiere autenticación
                        .requestMatchers("/api/**").authenticated()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                );

        // Añadimos filtros: autenticación primero, luego autorización (validación JWT)
        http.addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jWTAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager: delegamos en AuthenticationConfiguration para mantener compatibilidad
     * con las versiones modernas de Spring Security. Registramos el UserDetailsService y el
     * PasswordEncoder mediante AuthenticationManagerBuilder para asegurar que el password
     * encoding está correctamente configurado.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Obtenemos el AuthenticationManagerBuilder compartido por HttpSecurity y
        // registramos nuestro UserDetailsService y PasswordEncoder.
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Password encoder usando BCrypt (configurable strength si hace falta)
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CorsFilter configurado de forma segura:
     * - Permite peticiones desde el frontend (localhost:3000 por defecto)
     * - Se exponen las cabeceras Authorization para que el frontend pueda leerlas
     * - Permite credenciales (cookies, headers de autorización)
     * - Configurado para manejar preflight OPTIONS correctamente
     * 
     * IMPORTANTE: En producción, cambiar a orígenes específicos mediante FRONTEND_ORIGIN env var
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales (cookies, Authorization header)
        config.setAllowCredentials(true);

        // Configurar orígenes permitidos desde variable de entorno o defaults para desarrollo
        String originEnv = System.getenv().get("FRONTEND_ORIGIN");
        if (originEnv != null && !originEnv.isBlank()) {
            // Producción: usar origen específico desde env var
            config.addAllowedOrigin(originEnv);
        } else {
            // Desarrollo: permitir localhost en varios puertos
            config.addAllowedOrigin("http://localhost:3000");
            config.addAllowedOrigin("http://localhost:8080");
            config.addAllowedOrigin("http://localhost");
            config.addAllowedOrigin("http://127.0.0.1:3000");
            config.addAllowedOrigin("http://127.0.0.1:8080");
            config.addAllowedOrigin("http://127.0.0.1");
        }

        // Permitir todas las cabeceras (incluidas custom headers del frontend)
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, OPTIONS, etc.)
        config.addAllowedMethod("*");
        
        // Exponer headers que el frontend necesita leer (especialmente Authorization con el JWT)
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("X-Total-Count"); // Útil para paginación
        
        // Cache de preflight: 1 hora (reduce peticiones OPTIONS repetidas)
        config.setMaxAge(3600L);

        // Aplicar configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * AuthenticationEntryPoint que responde con JSON 401 cuando la autenticación falla.
     * Esto evita las respuestas HTML por defecto y es más amigable para clientes SPA.
     */
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
        };
    }

}
