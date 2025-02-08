package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmate.techmate.Entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    
} 
