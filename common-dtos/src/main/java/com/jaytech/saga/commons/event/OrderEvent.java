package com.jaytech.saga.commons.event;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Represents an event related to an order operation.
 * This class implements the Event interface to provide a common contract for all events in the system.
 */
@Data
@NoArgsConstructor
public class OrderEvent implements Event {

    /**
     * The unique identifier for order event.
     */
    private UUID eventId = UUID.randomUUID();

    /**
     * The timestamp when the event occurred.
     */
    private Date eventDate = new Date();

    /**
     * The OrderRequestDto containing information about the order operation.
     */
    private OrderRequestDto orderRequestDto;

    /**
     * The status of the order associated with the event.
     */
    private OrderStatus orderStatus;

    /**
     * Default constructor for creating an OrderEvent.
     * Note: The eventId and eventDate will be automatically generated.
     */
    public OrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        this.orderRequestDto = orderRequestDto;
        this.orderStatus = orderStatus;
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
