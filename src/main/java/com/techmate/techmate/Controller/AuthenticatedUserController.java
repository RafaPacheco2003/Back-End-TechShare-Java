package com.techmate.techmate.Controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techmate.techmate.dto.CurrentUserDTO;
import com.techmate.techmate.security.UserDetailsImpl;

/**
 * Controlador para endpoints accesibles por cualquier usuario autenticado
 */
@RestController
@RequestMapping("/user")
public class AuthenticatedUserController {

    /**
     * Obtiene la información del usuario autenticado actualmente
     * @return datos del usuario con su nombre, email y roles
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Debug logging
        System.out.println("=== DEBUG /user/me ===");
        System.out.println("Authentication object: " + authentication);
        System.out.println("Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "null"));
        System.out.println("Principal class: " + (authentication != null && authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null"));
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("ERROR: Usuario no autenticado");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            CurrentUserDTO currentUser = new CurrentUserDTO();
            currentUser.setId(userDetails.getId());
            currentUser.setUserName(userDetails.getUsername());
            currentUser.setFirstName(userDetails.getFirstName());
            currentUser.setLastName(userDetails.getLastName());
            currentUser.setEmail(userDetails.getEmail());
            currentUser.setRoles(userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()));
            
            System.out.println("SUCCESS: Returning user data for: " + userDetails.getUsername());
            return ResponseEntity.ok(currentUser);
        }
        
        System.out.println("ERROR: Principal no es UserDetailsImpl");
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuario");
    }
}
