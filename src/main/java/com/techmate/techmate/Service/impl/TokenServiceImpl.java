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
        return TokenUtils.getUserIdFromToken(token);
    }

    // Extraer los roles del usuario desde el token
    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }

    // Extraer el email del usuario desde el token
    @Override
    public String getUserEmailFromToken(String token) {
        Claims claims = TokenUtils.decodeToken(token);
        if (claims != null) {
            return claims.getSubject(); // El email está guardado en el 'subject'
        }
        return null;
    }
}
