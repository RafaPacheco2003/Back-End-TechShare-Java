package com.techmate.techmate.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

@RestController
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest registerRequest) {
        
        // Validar que el objeto no sea nulo
        if (registerRequest == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body cannot be empty"));
        }
        
        try {
            // Sanitizar inputs adicionales (defensa en profundidad)
            registerRequest.setUser_name(sanitizeInput(registerRequest.getUser_name()));
            registerRequest.setFirst_name(sanitizeInput(registerRequest.getFirst_name()));
            registerRequest.setLast_name(sanitizeInput(registerRequest.getLast_name()));
            registerRequest.setEmail(registerRequest.getEmail().toLowerCase().trim());
            
            // Si no se envían roles, asignar rol de usuario por defecto (ID 2)
            if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
                registerRequest.setRoles(Set.of(2)); // Rol de usuario normal
                log.info("No roles provided, assigning default role (USER)");
            } else {
                // Validar roles permitidos (prevenir escalada de privilegios)
                if (registerRequest.getRoles().stream().anyMatch(roleId -> roleId > 2)) {
                    log.warn("Attempt to register with unauthorized role: {}", registerRequest.getRoles());
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid role selection"));
                }
            }
            
            // Encode password
            registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            String msg = authService.registerUser(registerRequest);
            log.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.ok(Map.of("message", msg));
            
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    /**
     * Sanitiza input removiendo caracteres peligrosos
     */
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim()
                .replaceAll("[<>\"'&]", "") // Remover caracteres HTML peligrosos
                .replaceAll("\\p{Cntrl}", ""); // Remover caracteres de control
    }
}
