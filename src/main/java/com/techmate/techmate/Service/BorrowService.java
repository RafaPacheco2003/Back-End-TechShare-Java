package com.techmate.techmate.Service;

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.Entity.Status;

public interface BorrowService {
    
    BorrowDTO createBorrowDTO(BorrowDTO borrowDTO) throws Exception;

    void updateBorrowStatus(Integer borrowId, Status newStatus) throws Exception;
}
