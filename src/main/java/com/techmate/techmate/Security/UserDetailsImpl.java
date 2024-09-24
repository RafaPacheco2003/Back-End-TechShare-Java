package com.techmate.techmate.Security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techmate.techmate.Entity.Usuario;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public class UserDetailsImpl implements UserDetails{

    private final Usuario usuario;
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
       return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
       return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return usuario.getEmail();
    }
    


    public String getNombre(){
        return usuario.getNombre();
    }
}
