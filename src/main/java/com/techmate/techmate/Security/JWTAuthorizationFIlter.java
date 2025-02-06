package com.techmate.techmate.Security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.techmate.techmate.Entity.Role;

import com.techmate.techmate.Repository.RoleRepository;
import java.util.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthorizationFIlter extends OncePerRequestFilter{
     private final RoleRepository roleRepository;

    public JWTAuthorizationFIlter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.replace("Bearer ", "");
            UsernamePasswordAuthenticationToken usernamePAT = TokenUtils.getAuthentication(token);

            if (usernamePAT != null) {
                // Obtener los roles desde el token
                Optional<List<Integer>> rolesFromToken = TokenUtils.getRolesFromToken(token);

                if (rolesFromToken.isPresent()) {
                    List<Integer> roles = rolesFromToken.get();

                    // Verificar si el rol con id = 1 está presente en los roles del token
                    if (roles.contains(1)) {
                        // Si tiene el rol con id = 1, permitir el acceso
                        SecurityContextHolder.getContext().setAuthentication(usernamePAT);
                    } else {
                        // Si no tiene el rol con id = 1, denegar el acceso
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                } else {
                    // Si no se pueden obtener los roles, denegar el acceso
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Si no se puede autenticar
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
}