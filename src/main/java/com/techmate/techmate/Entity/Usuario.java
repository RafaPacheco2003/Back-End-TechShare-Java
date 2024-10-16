package com.techmate.techmate.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_name; // Cambiado de 'nombre' a 'user_name'
    private String first_name; // Nuevo campo
    private String last_name; // Nuevo campo
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_role", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "usuario_id"), // Columna que se refiere a Usuario
        inverseJoinColumns = @JoinColumn(name = "role_id") // Columna que se refiere a Role
    )
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movements> movements;
}
