package com.techmate.techmate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Permitimos peticiones OPTIONS sin autenticar para soportar CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String bearerToken = request.getHeader("Authorization");

        // Si no hay cabecera, continuamos sin autenticar (otros filtros / endpoints
        // decidirán si la ruta requiere autenticación y devolverán 401).
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = bearerToken.substring(7);

        try {
            // TokenUtils.getAuthentication verifica la firma y la expiración y
            // construye un UsernamePasswordAuthenticationToken con las autoridades.
            UsernamePasswordAuthenticationToken authentication = TokenUtils.getAuthentication(token);

            if (authentication == null) {
                // Token correcto estructuralmente pero inválido para nuestra app
                log.debug("Token válido pero no se pudo construir Authentication desde el token");
                respondUnauthorized(response, "Token inválido");
                return;
            }

            // Colocamos el Authentication en el SecurityContext para que el resto
            // de la cadena de filtros y los controladores puedan obtener el usuario.
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Registro y respuesta uniforme en JSON para errores de token
            log.warn("Error validando token JWT: {}", e.getMessage());
            respondUnauthorized(response, "Error validando token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> payload = new HashMap<>();
        payload.put("error", message);
        new ObjectMapper().writeValue(response.getWriter(), payload);
    }

}