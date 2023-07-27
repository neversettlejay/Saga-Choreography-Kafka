package com.jaytech.saga.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The UserTransaction class represents an entity that stores transaction details related to user payments.
 * This entity is used to keep track of the payment transactions made by users in the payment service.
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTransaction {

    /**
     * The orderId field represents the unique identifier for a transaction.
     * It is annotated with @Id, indicating that this field is the primary key for the UserTransaction entity.
     * The orderId uniquely identifies each UserTransaction record in the database.
     */
    @Id
    private Integer orderId;

    /**
     * The userId field represents the unique identifier for a user involved in the transaction.
     * It stores the user's ID who made the payment for the transaction.
     * This field is used to associate the transaction with a specific user in the payment service.
     */
    private int userId;

    /**
     * The amount field represents the payment amount for the transaction.
     * It stores the monetary value of the transaction, indicating the payment made by the user.
     * This field is used to record the payment amount for auditing and tracking purposes.
     */
    private int productPaymentAmount;
}
