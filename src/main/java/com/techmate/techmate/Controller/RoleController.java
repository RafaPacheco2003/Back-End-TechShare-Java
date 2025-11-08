package com.techmate.techmate.controller;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.security.AuthorizationUtils;
import com.techmate.techmate.service.RoleService;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("/admin/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Verifica que el usuario sea admin antes de procesar la solicitud.
     * Si no es admin, lanza una excepción 403 Forbidden.
     */
    private void checkAdminAccess() {
        if (!AuthorizationUtils.isUserAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acceso denegado. Se requieren permisos de administrador."
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRol(@RequestBody RoleDTO roleDTO) {
        checkAdminAccess();
        
        // Guardar el rol usando el servicio
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleByID(@PathVariable("id") Integer id) {
        checkAdminAccess();
        
        // Obtener el rol por ID
        RoleDTO role = roleService.getRoleById(id);
        if (role != null) {
            return new ResponseEntity<>(role, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable("id") Integer id,
            @RequestBody RoleDTO roleDTO) {
        checkAdminAccess();
        
        // Actualizar el rol con los datos del DTO
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
        if (updatedRole != null) {
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        checkAdminAccess();
        
        List<RoleDTO> roles = roleService.getAllRole(); // Llama al servicio para obtener todos los roles
        return new ResponseEntity<>(roles, HttpStatus.OK); // Retorna 200 OK con la lista de roles
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> cleanupRoleAssociations(@PathVariable int roleId) {
        checkAdminAccess();
        
        roleService.cleanupRoleAssociations(roleId);
        return ResponseEntity.ok("Las asociaciones para el rol con ID " + roleId + " fueron eliminadas correctamente.");
    }
    

}
