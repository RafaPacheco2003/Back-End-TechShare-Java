package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmate.techmate.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    
} 
