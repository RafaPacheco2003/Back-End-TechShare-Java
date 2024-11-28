package com.techmate.techmate.Controller.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.Service.TokenService;
@RestController
public class UserDemoController {
    

    @Autowired
    private TokenService tokenService;

    // Endpoint para obtener la información del usuario desde el token
    @GetMapping("/api/usuario/info")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioInfo(@RequestHeader("Authorization") String token) {
        // Eliminar "Bearer " del token
        String cleanToken = token.replace("Bearer ", "");
        
        Integer userId = tokenService.getUserIdFromToken(cleanToken);
        String email = tokenService.getUserEmailFromToken(cleanToken);
        var roles = tokenService.getRolesFromToken(cleanToken).orElse(List.of());

        // Construir respuesta
        Map<String, Object> usuarioInfo = new HashMap<>();
        usuarioInfo.put("userId", userId);
        usuarioInfo.put("email", email);
        usuarioInfo.put("roles", roles);

        return ResponseEntity.ok(usuarioInfo);
    }
}
