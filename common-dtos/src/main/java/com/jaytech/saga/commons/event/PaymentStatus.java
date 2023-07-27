package com.jaytech.saga.commons.event;

/**
 * Enumeration representing the status of a payment.
 * It defines two possible states: PAYMENT_COMPLETED and PAYMENT_FAILED.
 * This enum is used to track the outcome of a payment operation.
 */
public enum PaymentStatus {

    /**
     * Represents that the payment has been successfully completed.
     */
    PAYMENT_COMPLETED,

    /**
     * Represents that the payment has failed or was not successful.
     */
    PAYMENT_FAILED,

    /**
     * Represents that the payment status is unknown
     */
    PAYMENT_STATUS_UNKNOWN
}
