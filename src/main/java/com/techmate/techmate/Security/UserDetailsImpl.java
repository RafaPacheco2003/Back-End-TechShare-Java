package com.techmate.techmate.Security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.UsuarioRole;
import com.techmate.techmate.Repository.UsuarioRoleRepository;

import lombok.AllArgsConstructor;

/**
 * Implementación de la interfaz UserDetails para integrar la autenticación
 * de Spring Security con la entidad Usuario.
 * 
 * Esta clase convierte la información de la entidad Usuario en un formato 
 * que Spring Security entiende para realizar la autenticación y autorización.
 */
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
    private final UsuarioRoleRepository usuarioRoleRepository; // Repositorio para buscar los roles asociados al usuario.

    /**
     * Método para obtener el objeto Usuario completo.
     *
     * @return el objeto Usuario asociado a este UserDetails.
     */
    public Usuario getUsuario() {
        return this.usuario;
    }

    /**
     * Método para obtener el ID del usuario.
     *
     * @return ID del usuario (tipo Integer).
     */
    public Integer getId() {
        return usuario.getId();
    }

    /**
     * Método para obtener una lista de IDs de roles asociados al usuario.
     *
     * Utiliza el repositorio `UsuarioRoleRepository` para buscar los roles
     * de acuerdo al ID del usuario y mapea cada `UsuarioRole` al `idRole`.
     *
     * @return Lista de IDs de roles asociados.
     */
    public List<Integer> getIdRoles() {
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioId(usuario.getId());

        return usuarioRoles.stream()
                .map(UsuarioRole::getId) // Suponiendo que `getId()` devuelve el ID del rol asociado.
                .collect(Collectors.toList());
    }

    /**
     * Método para obtener los roles del usuario en forma de `GrantedAuthority`.
     *
     * @return Colección de roles (autoridades) asociadas al usuario.
     */
    public Collection<? extends GrantedAuthority> getRoles() {
        return getAuthorities();
    }

    /**
     * Implementación de `getAuthorities()` para Spring Security.
     *
     * Este método convierte cada rol del usuario a un `GrantedAuthority`,
     * necesario para la autorización en Spring Security.
     *
     * @return Colección de objetos `GrantedAuthority` basados en los roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuarioRoleRepository.findByUsuarioId(usuario.getId()).stream()
                .map(usuarioRole -> new SimpleGrantedAuthority(usuarioRole.getRole().getNombre()))
                .collect(Collectors.toList());
    }



    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario (String).
     */
    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /**
     * Obtiene el nombre de usuario (email en este caso).
     *
     * @return Email del usuario.
     */
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    

    /**
     * Método adicional para obtener el nombre completo del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return usuario.getUser_name();
    }

    // Otros métodos requeridos por UserDetails para la autenticación.
    @Override
    public boolean isAccountNonExpired() {
        return true; // Devuelve true, asumiendo que la cuenta no expira.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Devuelve true, asumiendo que la cuenta no está bloqueada.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Devuelve true, asumiendo que las credenciales no expiran.
    }

    @Override
    public boolean isEnabled() {
        return true; // Devuelve true, asumiendo que la cuenta está habilitada.
    }
}
