package com.techmate.techmate.Service.impl;

import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Service.TokenService;

import io.jsonwebtoken.Claims;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    // Extraer el ID del usuario desde el token
    @Override
    public Integer getUserIdFromToken(String token) {
        Claims claims = TokenUtils.decodeToken(token);
        if (claims != null) {
            Integer userId = (Integer) claims.get("id");
            System.out.println("User ID from token: " + userId); // Verificación del ID
            return userId;
        }
        return null;
    }

    // Extraer los roles del usuario desde el token
    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        // Llamamos al método de TokenUtils que extrae los roles del token
        return TokenUtils.getRolesFromToken(token);
    }

    // Extraer el email del usuario desde el token
    @Override
    public String getUserEmailFromToken(String token) {
        // Decodificar el token y obtener los claims
        Claims claims = TokenUtils.decodeToken(token);
        if (claims != null) {
            // El email está guardado en el 'subject' del token
            return claims.getSubject();
        }
        return null;
    }

    @Override
    public String getUserNameFromToken(String token) {
        // Llamamos al método de TokenUtils que extrae el nombre de usuario del token
        return TokenUtils.getUserNameFromToken(token);
    }

}
