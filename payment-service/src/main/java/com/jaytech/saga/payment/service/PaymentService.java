package com.jaytech.saga.payment.service;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.dto.PaymentRequestDto;
import com.jaytech.saga.commons.event.OrderEvent;
import com.jaytech.saga.commons.event.PaymentEvent;
import com.jaytech.saga.commons.event.PaymentStatus;
import com.jaytech.saga.payment.entity.UserBalance;
import com.jaytech.saga.payment.entity.UserTransaction;
import com.jaytech.saga.payment.repository.UserBalanceRepository;
import com.jaytech.saga.payment.repository.UserTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserTransactionRepository userTransactionRepository;

    // This method is annotated with @PostConstruct and will be executed after the PaymentService bean is created.
    // It initializes some user balances in the database for testing purposes.
    @PostConstruct
    public void initUserBalanceInDB() {
        userBalanceRepository.saveAll(Stream.of(new UserBalance(101, 5000),
                new UserBalance(102, 3000),
                new UserBalance(103, 4200),
                new UserBalance(104, 20000),
                new UserBalance(105, 999)).collect(Collectors.toList()));
    }

    /**
     * This method handles a new order event and processes the payment for the order.
     * It takes an OrderEvent object and returns a PaymentEvent object with the payment status.
     *
     * The steps in this method are as follows:
     * 1. Extract the order details from the OrderEvent and create a PaymentRequestDto.
     * 2. Check if the user's balance is sufficient for the payment.
     * 3. If the balance is sufficient, complete the payment, deduct the amount from the user's balance,
     *    and save a UserTransaction record for the payment in the database.
     * 4. If the balance is not sufficient, return a PaymentEvent with the status PAYMENT_FAILED.
     * 5. The method is annotated with @Transactional to ensure that the database changes are atomic and consistent.
     *    If an exception occurs during the transaction, all changes will be rolled back.
     *
     * @param orderEvent The OrderEvent object representing the new order request.
     * @return A PaymentEvent object indicating the payment status.
     */
    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();

        PaymentRequestDto paymentRequestDto =PaymentRequestDto.builder().orderId(orderRequestDto.getOrderId()).userId(orderRequestDto.getUserId()).amount(orderRequestDto.getProductPrice()).build();


        return userBalanceRepository.findById(orderRequestDto.getUserId())
                .filter(userBalanceBeforePurchase -> userBalanceBeforePurchase.getUserBalance() > orderRequestDto.getProductPrice())
                .map(userBalanceAfterPurchase -> {
                    // Deduct the payment amount from the user's balance.
                    userBalanceAfterPurchase.setUserBalance(userBalanceAfterPurchase.getUserBalance() - orderRequestDto.getProductPrice());

                    // Save a UserTransaction record for the successful payment in the database.
                    userTransactionRepository.save(UserTransaction.builder().orderId(orderRequestDto.getOrderId()).userId(orderRequestDto.getUserId()).productPaymentAmount(orderRequestDto.getProductPrice()).build());

                    // Return a PaymentEvent with the status PAYMENT_COMPLETED.
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
                }).orElse(new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_FAILED));

    }

    /**
     * This method cancels an order event and updates the user's balance accordingly.
     *
     * The steps in this method are as follows:
     * 1. Find the UserTransaction corresponding to the order from the orderEvent.
     * 2. Delete the UserTransaction record from the database.
     * 3. Find the UserBalance record for the user and update the balance by adding the canceled order amount.
     * 4. The method is annotated with @Transactional to ensure that the database changes are atomic and consistent.
     *    If an exception occurs during the transaction, all changes will be rolled back.
     *
     * @param orderEvent The OrderEvent object representing the order cancellation request.
     */
    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {

        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(userTransaction->{
                    // Delete the UserTransaction record from the database.
                    userTransactionRepository.delete(userTransaction);

                    // Update the user's balance by adding the canceled order amount.
                    userTransactionRepository.findById(userTransaction.getUserId())
                            .ifPresent(userBalance->userBalance.setProductPaymentAmount(userBalance.getProductPaymentAmount()+userTransaction.getProductPaymentAmount()));
                });
    }
}
