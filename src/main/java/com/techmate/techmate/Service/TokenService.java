package com.techmate.techmate.Service;
import java.util.*;

public interface TokenService {
    String createToken(Integer id, String email, List<String> roles, List<Integer> idRoles);
    Integer getUserIdFromToken(String token);
}
