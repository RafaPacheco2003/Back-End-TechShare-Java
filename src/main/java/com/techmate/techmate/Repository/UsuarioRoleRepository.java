package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.UsuarioRole;

import java.util.List;

@Repository
public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Integer> {
    // Método para obtener los roles asociados con un usuario por su id
    List<UsuarioRole> findByUsuarioId(Integer usuarioId);


    @Query("SELECT ur FROM UsuarioRole ur WHERE ur.usuario.id IN :usuarioIds")
    List<UsuarioRole> findByUsuarioIds(@Param("usuarioIds") List<Integer> usuarioIds);

    List<UsuarioRole> findByRole(Role role);
}
