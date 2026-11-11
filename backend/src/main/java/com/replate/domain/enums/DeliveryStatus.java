package com.replate.domain.enums;

/**
 * Delivery task execution lifecycle states:
 * PENDING_ASSIGNMENT / UNASSIGNED -> ASSIGNED -> ACCEPTED -> PICKED_UP -> IN_TRANSIT -> DELIVERED
 * Exceptional: REJECTED, CANCELLED, FAILED
 */
public enum DeliveryStatus {
    PENDING_ASSIGNMENT,
    UNASSIGNED,
    ASSIGNED,
    ACCEPTED,
    REJECTED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    FAILED
}
