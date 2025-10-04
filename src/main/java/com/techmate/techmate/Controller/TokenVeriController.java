package com.techmate.techmate.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.dto.VerificationResponse;
import com.techmate.techmate.Service.VerificationService;

@RestController
public class TokenVeriController {

    private final VerificationService verificationService;

    public TokenVeriController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        VerificationResponse result = verificationService.verifyToken(token);
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
        return ResponseEntity.ok(result.getMessage());
    }

}
