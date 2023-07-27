package com.jaytech.saga.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The UserBalance class represents an entity that stores the balance of a user.
 * This entity is used to keep track of the user's balance in the payment service.
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBalance {

    /**
     * The userId field represents the unique identifier for a user.
     * It is annotated with @Id, indicating that this field is the primary key for the UserBalance entity.
     * The userId uniquely identifies each UserBalance record in the database.
     */
    @Id
    private int userId;

    /**
     * The price field represents the balance amount of the user.
     * It stores the amount of money available in the user's account.
     * This field is used to keep track of the user's balance for payment transactions.
     */
    private int userBalance;
}
