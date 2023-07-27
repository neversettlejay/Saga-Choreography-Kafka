package com.jaytech.saga.commons.event;

/**
 * Enumeration representing the status of an order.
 * It defines three possible states: ORDER_CREATED, ORDER_COMPLETED, and ORDER_CANCELLED.
 * This enum is used to track the progress of an order and provide information about its current state.
 */
public enum OrderStatus {

    /**
     * Represents that the order has been created but not yet completed or cancelled.
     */
    ORDER_CREATED,

    /**
     * Represents that the order has been successfully completed.
     */
    ORDER_COMPLETED,

    /**
     * Represents that the order has been cancelled.
     */
    ORDER_CANCELLED
}
