package com.techmate.techmate.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener for Material-related domain events.
 * All event handlers are async to avoid blocking the main request thread.
 * 
 * This demonstrates how to decouple business logic from side effects:
 * - Material service focuses on business rules
 * - Event listeners handle notifications, logging, etc.
 */
@Slf4j
@Component
public class MaterialEventListener {
    
    /**
     * Handles MaterialLowStockEvent asynchronously.
     * This could be extended to:
     * - Send email to admins
     * - Create dashboard alerts
     * - Trigger automatic purchase orders
     */
    @Async
    @EventListener
    public void handleLowStock(MaterialLowStockEvent event) {
        log.warn(
            "🚨 LOW STOCK ALERT - Material '{}' (ID: {}) has only {} units (threshold: {}). EventID: {}",
            event.getMaterialName(),
            event.getMaterialId(),
            event.getCurrentStock(),
            event.getThreshold(),
            event.getEventId()
        );
        
        // TODO: Implement notification logic
        // - emailService.sendLowStockAlert(event);
        // - dashboardService.createAlert(event);
        // - purchaseOrderService.createAutomaticOrder(event);
    }
}
