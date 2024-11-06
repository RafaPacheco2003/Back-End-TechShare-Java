package com.techmate.techmate.Security;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenUtils {

    private final static String ACCESS_TOKEN_SECRET = "uD1Fzv9pJ2GU8y2T7mLnOiZmQg3JsX5R9B8PslDFNc";
    
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_000L; // 30 días

    // Método para crear el token
    // Modifica el método para aceptar el id del usuario
    public static String createToken(Integer id, String email, List<String> roles, List<Integer> idRoles) {
        Long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        // Añade el id a los claims personalizados
        Map<String, Object> extra = new HashMap<>();
        extra.put("id", id);   // Aquí se añade el id del usuario
        extra.put("roles", roles); // Aquí se añaden los roles del usuario
        extra.put("idRoles", idRoles);
        System.out.println("Creando token para ID: " + id); // Verifica el ID antes de crear el token

        SecretKey secretKey = Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)  // Añadir los claims extra (id, nombre, roles)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
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
            List<String> roles = (List<String>) claims.get("roles"); // Obtener roles del token
            
            // Convertir roles a authorities
            var authorities = roles.stream()
                                   .map(role -> new SimpleGrantedAuthority(role))
                                   .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        } catch (JwtException e) {
            return null; // Retorna null si hay un error con el token
        }
    }

    // Método para obtener el rol del usuario autenticado
    public static String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Aquí puedes obtener el rol o roles del usuario autenticado
            return userDetails.getAuthorities().stream()
                              .findFirst() // Ajusta si el usuario puede tener varios roles
                              .map(GrantedAuthority::getAuthority)
                              .orElse(null); // Devuelve el rol o null si no tiene ninguno
        }
        return null;
    }
    

    public static Integer getUserIdFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            System.out.println("Claims: " + claims); // Imprime los reclamos
            
            // Recuperar el id de los claims
            Object idClaim = claims.get("id");
            System.out.println("ID claim desde claims: " + idClaim); // Imprime el claim del ID
    
            // Si el idClaim no es nulo, intenta convertirlo a Integer
            if (idClaim != null) {
                Integer userId;
                try {
                    userId = (Integer) idClaim; // Intenta obtener el ID como Integer
                } catch (ClassCastException e) {
                    // Si el id está almacenado como String, conviértelo a Integer
                    userId = Integer.parseInt(idClaim.toString());
                }
                System.out.println("ID de usuario extraído del token: " + userId); // Imprime el ID del usuario
                return userId; // Retorna el ID del usuario
            }
        }
        throw new RuntimeException("Token no válido o ID no encontrado");
    }
    
    
    
    
    /*public static Integer getUserIdFromToken(String token) {
        Claims claims = decodeToken(token);
        if (claims != null) {
            System.out.println("Claims: " + claims); // Imprime los reclamos
            Integer userId;
            try {
                userId = claims.get("id", Integer.class); // Intenta obtener el ID como Integer
            } catch (ClassCastException e) {
                // Si el id está almacenado como String, conviértelo a Integer
                userId = Integer.parseInt(claims.get("id").toString());
            }
            System.out.println("ID de usuario extraído del token: " + userId); // Imprime el ID del usuario
            return userId;
        }
        throw new RuntimeException("Token no válido");
    }*/
    
    
    

    public static Claims decodeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            System.out.println("Claims decodificados: " + claims); // Verifica los claims decodificados
            return claims;
        } catch (JwtException e) {
            // Manejo de excepción si el token no es válido
            System.out.println("Error al decodificar el token: " + e.getMessage());
            return null;
        }
    }
    
}
