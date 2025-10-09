package com.techmate.techmate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener for Borrow-related domain events.
 * Handles notifications and side effects asynchronously.
 */
@Slf4j
@Component
public class BorrowEventListener {
    
    /**
     * Handles BorrowCreatedEvent asynchronously.
     * This could be extended to:
     * - Send confirmation email to user
     * - Log to audit system
     * - Update dashboards
     */
    @Async
    @EventListener
    public void handleBorrowCreated(BorrowCreatedEvent event) {
        log.info(
            "✅ BORROW CREATED - User {} created borrow {} with amount ${:.2f}. EventID: {}",
            event.getUserId(),
            event.getBorrowId(),
            event.getAmount(),
            event.getEventId()
        );
        
        // TODO: Implement notification logic
        // - emailService.sendBorrowConfirmation(event);
        // - auditService.logBorrow(event);
        // - dashboardService.updateStats(event);
    }
    
    /**
     * Handles BorrowReturnedEvent asynchronously.
     * This could be extended to:
     * - Send return confirmation email
     * - Apply late fees if applicable
     * - Update user statistics
     */
    @Async
    @EventListener
    public void handleBorrowReturned(BorrowReturnedEvent event) {
        if (event.isWasLate()) {
            log.warn(
                "⚠️ LATE RETURN - Borrow {} was returned late. User: {}. EventID: {}",
                event.getBorrowId(),
                event.getUserId(),
                event.getEventId()
            );
            
            // TODO: Handle late returns
            // - penaltyService.applyLateFee(event);
            // - emailService.sendLateReturnNotification(event);
        } else {
            log.info(
                "✅ BORROW RETURNED - Borrow {} returned on time. User: {}. EventID: {}",
                event.getBorrowId(),
                event.getUserId(),
                event.getEventId()
            );
        }
        
        // TODO: Implement notification logic
        // - emailService.sendReturnConfirmation(event);
        // - userService.updateUserStats(event);
    }
}
