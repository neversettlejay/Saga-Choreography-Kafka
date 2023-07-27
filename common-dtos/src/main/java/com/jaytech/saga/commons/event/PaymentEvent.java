package com.jaytech.saga.commons.event;

import com.jaytech.saga.commons.dto.PaymentRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Represents an event related to a payment operation.
 * This class implements the Event interface to provide a common contract for all events in the system.
 */
@Data
@NoArgsConstructor
public class PaymentEvent implements Event {

    /**
     * The unique identifier for payment event.
     */
    private UUID eventId = UUID.randomUUID();

    /**
     * The timestamp when the event occurred.
     */
    private Date eventDate = new Date();

    /**
     * The PaymentRequestDto containing information about the payment operation.
     */
    private PaymentRequestDto paymentRequestDto;

    /**
     * The status of the payment associated with the event.
     */
    private PaymentStatus paymentStatus;

    /**
     * Default constructor for creating a PaymentEvent.
     * Note: The eventId and eventDate will be automatically generated.
     */
    public PaymentEvent(PaymentRequestDto paymentRequestDto, PaymentStatus paymentStatus) {
        this.paymentRequestDto = paymentRequestDto;
        this.paymentStatus = paymentStatus;
    }

    /**
     * Get the unique identifier of the event.
     *
     * @return The unique identifier of the event as a UUID.
     */
    @Override
    public UUID getEventId() {
        return eventId;
    }

    /**
     * Get the timestamp when the event occurred.
     *
     * @return The date when the event occurred.
     */
    @Override
    public Date getDate() {
        return eventDate;
    }
}
