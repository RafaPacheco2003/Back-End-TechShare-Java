package com.techmate.techmate.security;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

public class TokenUtils {

    // Leer secret y expiración desde variables de entorno para no dejar secretos en el repo
    private static final String ACCESS_TOKEN_SECRET = getJwtSecret();

    // Validez en segundos (por defecto 3600 = 1 hora). Se puede configurar con JWT_EXPIRATION_SECONDS
    private static final Long ACCESS_TOKEN_VALIDITY_SECONDS;
    static {
        Long defaultSeconds = 3600L;
        String s = System.getenv("JWT_EXPIRATION_SECONDS");
        if (s != null && !s.isBlank()) {
            try { defaultSeconds = Long.parseLong(s); } catch (NumberFormatException ignore) {}
        }
        ACCESS_TOKEN_VALIDITY_SECONDS = defaultSeconds;
    }

    // Método para crear el token
    // Modifica el método para aceptar el id del usuario
    public static String createToken(Integer id, String email, String userName, List<String> roles, List<Integer> idRoles) {
        Long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
    
        // Añadir el id, nombre de usuario, roles, y idRoles a los claims personalizados
        Map<String, Object> extra = new HashMap<>();
        extra.put("id", id);   // Aquí se añade el id del usuario
        extra.put("user_name", userName); // Aquí se añade el nombre de usuario
        extra.put("roles", roles); // Aquí se añaden los roles del usuario
        extra.put("idRoles", idRoles);
    
    SecretKey secretKey = getSecretKey();

    return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)  // Añadir los claims extra (id, nombre de usuario, roles, idRoles)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    

    // Método para obtener la autenticación a partir del token
    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles"); // Obtener roles del token
            if (roles == null) roles = List.of();
            // Convertir roles a authorities con prefijo ROLE_ para Spring Security
            var authorities = roles.stream()
                                   .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                                   .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        } catch (JwtException e) {
            return null; // Retorna null si hay un error con el token
        }
    }

    // Método para obtener el rol del usuario autenticado
    public static String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            // Leer directamente desde authorities sin depender del tipo de principal
            return authentication.getAuthorities().stream()
                              .findFirst() // Ajusta si el usuario puede tener varios roles
                              .map(GrantedAuthority::getAuthority)
                              .orElse(null); // Devuelve el rol o null si no tiene ninguno
        }
        return null;
    }
    

    public static Integer getUserIdFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            // Recuperar el id de los claims
            Object idClaim = claims.get("id");
            if (idClaim != null) {
                try {
                    return (Integer) idClaim;
                } catch (ClassCastException e) {
                    return Integer.parseInt(idClaim.toString());
                }
            }
        }
        throw new RuntimeException("Token no válido o ID no encontrado");
    }
    
    public static String getUserNameFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            Object userNameClaim = claims.get("user_name");
            if (userNameClaim != null) return userNameClaim.toString();
        }
        throw new RuntimeException("Token no válido o nombre de usuario no encontrado");
    }
    
    

    public static Optional<List<Integer>> getRolesFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            Object rolesClaim = claims.get("idRoles");
            if (rolesClaim != null) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Object> rawRoles = (List<Object>) rolesClaim;
                    // Convertir cada elemento a Integer de forma segura
                    List<Integer> roles = rawRoles.stream()
                        .map(obj -> {
                            if (obj instanceof Integer) return (Integer) obj;
                            if (obj instanceof String) return Integer.parseInt((String) obj);
                            throw new IllegalArgumentException("Invalid role type: " + obj.getClass());
                        })
                        .collect(Collectors.toList());
                    return Optional.of(roles);
                } catch (Exception e) {
                    // Log the error but return empty
                    System.err.println("Error parsing roles from token: " + e.getMessage());
                }
            }
        }
        return Optional.empty(); // Devuelve un Optional vacío si no se encuentra
    }
    
    

    public static Claims decodeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (JwtException e) {
            return null;
        }
    }
    
    private static String getJwtSecret() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            // En desarrollo local, permitir fallback con advertencia
            String activeProfile = System.getProperty("spring.profiles.active", "dev");
            if ("dev".equals(activeProfile) || "test".equals(activeProfile)) {
                System.err.println("⚠️  ADVERTENCIA: Usando JWT_SECRET por defecto. Configura JWT_SECRET en producción.");
                return "uD1Fzv9pJ2GU8y2T7mLnOiZmQg3JsX5R9B8PslDFNc"; // Solo para desarrollo
            } else {
                throw new IllegalStateException("JWT_SECRET es requerido en producción. Configura la variable de entorno JWT_SECRET.");
            }
        }
        return secret;
    }

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes());
    }
    
}
