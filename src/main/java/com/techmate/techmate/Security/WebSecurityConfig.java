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

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthorizationFIlter jWTAuthorizationFIlter;
    private final UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authnManager) throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authnManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");
    
        return http
                .csrf().disable()
                .authorizeHttpRequests()  // Cambia authorizeRequests() a authorizeHttpRequests()
                    .requestMatchers("/register").permitAll()  // Cambia antMatchers() a requestMatchers()
                    .anyRequest().authenticated()  // Cualquier otra ruta requiere autenticación
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless, adecuado para JWT
                .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jWTAuthorizationFIlter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    
    
    // Registrar AuthenticationManager como un bean
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


    public static void main(String [] args){
        System.out.println("pass " + new BCryptPasswordEncoder().encode("12345"));
    }
}
