package com.techmate.techmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;  // Auto-mapea a role_id

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    @Column(name = "nombre", unique = true, nullable = false)  // Mantener: columna NO sigue convención (debería ser 'name')
    private String nombre;

    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleMaterials> roleMaterials;


    // Método para agregar materiales a un rol
   
}
