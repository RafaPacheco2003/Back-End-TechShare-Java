package com.techmate.techmate.Service.impl;

import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Service.TokenService;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public String createToken(Integer id, String email, List<String> roles, List<Integer> idRoles) {
        return TokenUtils.createToken(id, email, roles, idRoles);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }
}
