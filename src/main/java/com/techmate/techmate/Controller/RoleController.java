package com.techmate.techmate.Controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.DTO.RoleDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Service.RoleService;

import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = "https://tech-share.vercel.app") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<RoleDTO> createRol(@RequestBody RoleDTO roleDTO) {
        try {
            // Guardar el rol usando el servicio
            RoleDTO createdRole = roleService.createRole(roleDTO);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejar excepciones y devolver respuesta de error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleByID(@PathVariable("id") Integer id) {
        try {
            // Obtener el rol por ID
            RoleDTO role = roleService.getRoleById(id);
            if (role != null) {
                return new ResponseEntity<>(role, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable("id") Integer id,
            @RequestBody RoleDTO roleDTO) {
        try {
            // Actualizar el rol con los datos del DTO
            RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
            if (updatedRole != null) {
                return new ResponseEntity<>(updatedRole, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        try {
            List<RoleDTO> roles = roleService.getAllRole(); // Llama al servicio para obtener todos los roles
            return new ResponseEntity<>(roles, HttpStatus.OK); // Retorna 200 OK con la lista de roles
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> cleanupRoleAssociations(@PathVariable int roleId) {
        roleService.cleanupRoleAssociations(roleId);
        return ResponseEntity.ok("Las asociaciones para el rol con ID " + roleId + " fueron eliminadas correctamente.");
    }
    

}
