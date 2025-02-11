package com.techmate.techmate.Security;

import java.util.*; // Importa todas las clases de java.util, incluyendo List y Collection
import java.util.stream.Collectors; // Importa Collectors para usar collect
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación basado en JWT (JSON Web Token) que extiende
 * UsernamePasswordAuthenticationFilter para manejar el proceso de login.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        AuthCredentials authCredentials = new AuthCredentials();

        try {
            authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword(),
                Collections.emptyList());

        return getAuthenticationManager().authenticate(usernamePAT);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        Integer userId = userDetails.getUsuario().getId();

         // Verificar si el usuario está verificado
         /*if (!userDetails.getUsuario().isEnabled()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // O el código de estado que prefieras
            response.getWriter().write("Cuenta no verificada. Por favor verifica tu cuenta.");
            return; 
        }
*/
        List<Integer> roleIdList = userDetails.getIdRoles();

        // Obtén los roles del usuario
        Collection<? extends GrantedAuthority> roles = userDetails.getRoles();

        // Convierte los roles a una lista de strings
        List<String> roleList = roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Aquí pasamos el username también al token, ahora que el método createToken lo
        // acepta
        String userName = userDetails.getNombre(); // Obtener el nombre de usuario

        // Genera el token JWT usando el id, username (email), userName, roles y idRoles
        String token = TokenUtils.createToken(userId, userDetails.getUsername(), userName, roleList, roleIdList);

        // Añade el token JWT a la cabecera de la respuesta con el prefijo "Bearer"
        response.addHeader("Authorization", "Bearer " + token);

        // Flushea el buffer de salida (envía inmediatamente la respuesta)
        response.getWriter().flush();

        // Llama al método de la clase padre para completar la autenticación
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
