package com.techmate.techmate.DTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.Movements;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Integer id;
    private String userName; // Correspondiente a 'user_name' en la entidad
    private String firstName; // Correspondiente a 'first_name' en la entidad
    private String lastName;  // Correspondiente a 'last_name' en la entidad
    private String email;
    private Set<String> roles; // Convertido a un Set de Strings para simplificar los roles

}
