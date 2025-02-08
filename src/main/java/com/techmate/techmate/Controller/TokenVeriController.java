package com.techmate.techmate.Controller;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.VerificationToken;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Repository.VerificationTokenRepository;

@RestController
public class TokenVeriController {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/verify")
public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
        return ResponseEntity.badRequest().body("Token inválido.");
    }

    Usuario usuario = verificationToken.getUsuario();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        return ResponseEntity.badRequest().body("Token caducado.");
    }

    usuario.setEnabled(true);
    usuarioRepository.save(usuario);

    return ResponseEntity.ok("Cuenta verificada con éxito. Ahora puedes iniciar sesión.");
}

    
}
