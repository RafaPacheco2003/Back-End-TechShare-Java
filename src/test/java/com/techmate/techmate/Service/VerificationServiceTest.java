package com.techmate.techmate.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;

import com.techmate.techmate.Service.VerificationService;
import com.techmate.techmate.dto.VerificationResponse;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class VerificationServiceTest {

    @Mock
    VerificationTokenRepository verificationTokenRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    VerificationService verificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        verificationService = new VerificationService(verificationTokenRepository, usuarioRepository);
    }

    @Test
    void verifyToken_notFound() {
        when(verificationTokenRepository.findByToken(anyString())).thenReturn(null);

        VerificationResponse res = verificationService.verifyToken("missing-token");

        assertFalse(res.isSuccess());
        assertEquals("Token inválido.", res.getMessage());
    }

    @Test
    void verifyToken_expired() {
        VerificationToken token = new VerificationToken();
        Calendar cal = Calendar.getInstance();
        // set expiry in the past
        cal.add(Calendar.MINUTE, -10);
        token.setExpiryDate(cal.getTime());

        when(verificationTokenRepository.findByToken(anyString())).thenReturn(token);

        VerificationResponse res = verificationService.verifyToken("expired-token");

        assertFalse(res.isSuccess());
        assertEquals("Token caducado.", res.getMessage());
    }

    @Test
    void verifyToken_success() {
        VerificationToken token = new VerificationToken();
        Calendar cal = Calendar.getInstance();
        // set expiry in the future
        cal.add(Calendar.MINUTE, 10);
        token.setExpiryDate(cal.getTime());

        Usuario user = new Usuario();
        user.setEnabled(false);
        token.setUsuario(user);

        when(verificationTokenRepository.findByToken(anyString())).thenReturn(token);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        VerificationResponse res = verificationService.verifyToken("valid-token");

        assertTrue(res.isSuccess());
        assertEquals("Cuenta verificada con éxito. Ahora puedes iniciar sesión.", res.getMessage());
        assertTrue(user.isEnabled());
        verify(usuarioRepository, times(1)).save(user);
    }
}
