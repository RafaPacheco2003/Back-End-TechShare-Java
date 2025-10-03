package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmate.techmate.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);
}
