/**
 * PurchaseOrder entity represents a purchase order in the system.
 * It is mapped to the "PURCHASE_ORDER_TBL" table in the database.
 */
package com.jaytech.saga.order.entity;

import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.commons.event.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PURCHASE_ORDER_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrder {

    /**
     * The unique identifier for the purchase order.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * The ID of the user who placed the order.
     */
    private Integer userId;

    /**
     * The ID of the product being purchased in the order.
     */
    private Integer productId;

    /**
     * The price of the product in the order.
     */
    private Integer price;

    /**
     * The status of the order, represented by an enum from OrderStatus.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * The payment status of the order, represented by an enum from PaymentStatus.
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
