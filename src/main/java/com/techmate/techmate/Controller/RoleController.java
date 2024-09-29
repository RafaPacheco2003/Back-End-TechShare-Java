package com.techmate.techmate.Controller;

import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
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

import com.techmate.techmate.DTO.RoleDTO;
import com.techmate.techmate.Service.RoleService;

@RestController
@RequestMapping("admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<RoleDTO> createRol(@RequestBody RoleDTO roleDTO) {
        // Guardar el rol usando el servicio
        RoleDTO createdRole = roleService.createRole(roleDTO);
    
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleByID(@PathVariable("id") Integer id) {

        // Create DTO Role
        RoleDTO role = roleService.getRoleById(id);

        return new ResponseEntity<>(role, HttpStatus.OK);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable("id") Integer id,
            @RequestBody RoleDTO roleDTO) {

        // Actualizar el rol con los datos del DTO
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);

        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Integer id) {
        try {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);// 204 No Content
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);// Retorna 404
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRole(); // Llama al servicio para obtener todos los roles
        return new ResponseEntity<>(roles, HttpStatus.OK); // Retorna 200 OK con la lista de roles
    }


}
