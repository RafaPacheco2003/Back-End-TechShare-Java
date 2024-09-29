package com.techmate.techmate.Security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.UsuarioRole;
import com.techmate.techmate.Repository.UsuarioRoleRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
    private final UsuarioRoleRepository usuarioRoleRepository; // Repositorio para buscar los roles

    public Usuario getUsuario() {
        return this.usuario; // Devuelve el objeto Usuario para acceder a sus propiedades, incluido el id
    }

    // Método para obtener el id del usuario
    public Integer getId() {
        return usuario.getId(); // Asumiendo que el id es de tipo Integer
    }

    // Método para obtener los ids de los roles asociados al usuario
    public List<Integer> getIdRoles() {
        // Obtener los roles asociados al usuario usando el repositorio
        // `UsuarioRoleRepository`
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioId(usuario.getId());

        // Mapear los ids de los roles
        return usuarioRoles.stream()
                .map(UsuarioRole::getId) // Asumiendo que `getId()` devuelve el `idRole` de la entidad `UsuarioRole`
                .collect(Collectors.toList());
    }

    // Método para obtener los roles asociados al usuario
    public Collection<? extends GrantedAuthority> getRoles() {
        return getAuthorities(); // Devuelve las autoridades (roles) asociados al usuario
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Obtener los roles asociados al usuario usando el repositorio
        // `UsuarioRoleRepository`
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioId(usuario.getId());

        // Mapear los roles a `GrantedAuthority`
        return usuarioRoles.stream()
                .map(usuarioRole -> new SimpleGrantedAuthority(usuarioRole.getRole().getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    public String getNombre() {
        return usuario.getNombre();
    }
}
