package com.techmate.techmate.Service.User;

import java.util.*;

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.Entity.Status;

public interface BorrowUserService {
    
    BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception;

    
    List<BorrowDTO> getAllBorrowsByUserId(Integer userId);


    /*
     * Token
     */
    Integer getUserIdFromToken(String token);
    Optional<List<Integer>> getRolesFromToken(String token);
}
