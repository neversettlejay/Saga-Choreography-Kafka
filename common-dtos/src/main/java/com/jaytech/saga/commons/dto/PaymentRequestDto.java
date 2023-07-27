package com.jaytech.saga.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a payment request for a specific order.
 * This DTO is used to pass the necessary information required to process a payment.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDto {

    /**
     * The ID of the order for which the payment is being made.
     */
    private Integer orderId;

    /**
     * The ID of the user making the payment.
     */
    private Integer userId;

    /**
     * The payment amount.
     */
    private Integer amount;
}
