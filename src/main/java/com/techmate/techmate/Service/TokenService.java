package com.techmate.techmate.Service;
import java.util.*;

public interface TokenService {
    Integer getUserIdFromToken(String token);

    Optional<List<Integer>> getRolesFromToken(String token);

    String getUserEmailFromToken(String token);
}
