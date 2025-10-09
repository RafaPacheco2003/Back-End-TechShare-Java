package com.techmate.techmate.event;

import com.techmate.techmate.entity.Borrow;
import lombok.Getter;

import java.util.Date;

/**
 * Event fired when a borrow is returned (completed).
 * This event can trigger:
 * - Email confirmation
 * - Stock restoration notifications
 * - Late return penalties (if applicable)
 */
@Getter
public class BorrowReturnedEvent extends DomainEvent {
    
    private final Integer borrowId;
    private final Integer userId;
    private final Date returnDate;
    private final Date endDate;
    private final boolean wasLate;
    
    public BorrowReturnedEvent(Borrow borrow, Date returnDate) {
        super(borrow);
        this.borrowId = borrow.getBorrowId();
        this.userId = borrow.getUsuario() != null ? borrow.getUsuario().getId() : null;
        this.returnDate = returnDate;
        this.endDate = borrow.getEndDate();
        this.wasLate = returnDate != null && endDate != null && returnDate.after(endDate);
    }
    
    @Override
    public String getEventType() {
        return "BORROW_RETURNED";
    }
    
    @Override
    public String toString() {
        return String.format(
            "BorrowReturnedEvent[borrowId=%d, userId=%d, wasLate=%b, eventId=%s]",
            borrowId, userId, wasLate, getEventId()
        );
    }
}
