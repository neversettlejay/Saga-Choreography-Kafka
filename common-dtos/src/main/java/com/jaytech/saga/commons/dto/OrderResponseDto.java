package com.jaytech.saga.commons.dto;

import com.jaytech.saga.commons.event.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the response for an order operation.
 * This DTO is used to transfer information about the order back to the caller after processing an order request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

    /**
     * The ID of the user who placed the order.
     */
    private Integer userId;

    /**
     * The ID of the product being purchased.
     */
    private Integer productId;

    /**
     * The amount of the product ordered.
     */
    private Integer amount;

    /**
     * The ID of the order.
     */
    private Integer orderId;

    /**
     * The status of the order.
     */
    private OrderStatus orderStatus;
}
