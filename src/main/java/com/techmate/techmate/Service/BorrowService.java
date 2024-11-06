package com.techmate.techmate.Service;

import java.util.*;



import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.Entity.Status;

public interface BorrowService {
    void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception;

    List<BorrowDTO> getAllBorrowDTO();

    List<BorrowDTO> getBorrowByStatus(String status);

    List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate);


    
     /*
     * Token
     */
    Integer getUserIdFromToken(String token);

   
}


