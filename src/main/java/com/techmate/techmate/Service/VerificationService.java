package com.techmate.techmate.Service;

import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.techmate.techmate.dto.VerificationResponse;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;

@Service
public class VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public VerificationService(VerificationTokenRepository verificationTokenRepository, UsuarioRepository usuarioRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public VerificationResponse verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return new VerificationResponse(false, "Token inválido.");
        }
        Usuario usuario = verificationToken.getUsuario();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return new VerificationResponse(false, "Token caducado.");
        }

    // Activar la cuenta: usamos el setter generado por Lombok para el campo isEnabled
    usuario.setEnabled(true);
        usuarioRepository.save(usuario);

        return new VerificationResponse(true, "Cuenta verificada con éxito. Ahora puedes iniciar sesión.");
    }
}
