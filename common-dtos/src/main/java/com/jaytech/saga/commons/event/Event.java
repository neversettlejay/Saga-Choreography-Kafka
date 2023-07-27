package com.jaytech.saga.commons.event;

import java.util.Date;
import java.util.UUID;

/**
 * Interface representing an event that occurred in the system.
 * All events within the system should implement this interface to ensure a consistent contract.
 */
public interface Event {

    /**
     * Get the unique identifier of the event.
     *
     * @return The unique identifier of the event as a UUID.
     */
    UUID getEventId();

    /**
     * Get the timestamp when the event occurred.
     *
     * @return The date when the event occurred.
     */
    Date getDate();
}
