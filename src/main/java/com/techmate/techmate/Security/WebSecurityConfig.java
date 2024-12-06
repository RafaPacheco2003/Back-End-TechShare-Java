package com.techmate.techmate.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.AllArgsConstructor;import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebSecurityConfig {

    private final JWTAuthorizationFIlter jWTAuthorizationFIlter;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(JWTAuthorizationFIlter jWTAuthorizationFIlter, UserDetailsService userDetailsService) {
        this.jWTAuthorizationFIlter = jWTAuthorizationFIlter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authnManager) throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authnManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return http
                .cors() // Aplicar configuración de CORS
                .configurationSource(corsConfigurationSource()) // Enlazar configuración de CORS
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/register",
                        "/categories/images/**",
                        "/admin/materials/images/**",
                        "/subcategories/images/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jWTAuthorizationFIlter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Permitir credenciales
        config.addAllowedOrigin("https://tech-share.vercel.app"); // Permitir el origen de producción
        config.addAllowedOrigin("http://localhost:3000"); // Permitir localhost para pruebas locales
        config.addAllowedHeader("*"); // Permitir todos los headers
        config.addAllowedMethod("*"); // Permitir todos los métodos HTTP
        config.addExposedHeader("Authorization"); // Exponer encabezado Authorization

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
