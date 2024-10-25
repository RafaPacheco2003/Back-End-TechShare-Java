package com.techmate.techmate.Controller;



import com.techmate.techmate.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tokens")
public class TokenController {
    
    @Autowired
    private TokenService tokenService;

    // Endpoint para extraer el ID del token
    @GetMapping("/userId")
    public ResponseEntity<Integer> getUserIdFromToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // El token en el encabezado suele venir con el prefijo "Bearer ", así que debemos quitarlo
        String bearerToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        Integer userId = tokenService.getUserIdFromToken(bearerToken);
        return ResponseEntity.ok(userId);
    }
}

