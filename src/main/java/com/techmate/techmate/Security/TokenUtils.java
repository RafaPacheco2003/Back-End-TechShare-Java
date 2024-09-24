package com.techmate.techmate.Security;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
public class TokenUtils {
    
    private final static String ACCESS_TOKEN_SECRET = "uD1Fzv9pJ2GU8y2T7mLnOiZmQg3JsX5R9B8PslDFNc";

    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_000L; // 30 días


    // Método para crear el token
    public static String createToken(String nombre, String email){
        Long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nombre", nombre);
        

        // Generamos la clave secreta a partir de la cadena
        SecretKey secretKey = Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(email)  // El subject será el email del usuario
                .setExpiration(expirationDate)  // Fecha de expiración
                .addClaims(extra)   // Agregas los claims personalizados
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Firmas el token con el secreto y el algoritmo HS256
                .compact();  // Generas el JWT
    }

    // Método para obtener la autenticación a partir del token
    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
    
            String email = claims.getSubject();
            // Si tienes roles, debes obtenerlos aquí y pasarlos a la creación del token
            return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        } catch (JwtException e) {
            return null; // Retorna null si hay un error con el token
        }
    }
    
}