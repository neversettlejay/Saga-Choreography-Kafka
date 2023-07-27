package com.jaytech.saga.order.config;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.commons.event.PaymentStatus;
import com.jaytech.saga.order.entity.PurchaseOrder;
import com.jaytech.saga.order.repository.OrderRepository;
import com.jaytech.saga.order.service.OrderStatusPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Order Status Update Handler Configuration
 *
 * This configuration class provides a handler for updating the status of purchase orders based on their payment status.
 * The handler is responsible for processing order updates and publishing corresponding order events if necessary.
 */
@Configuration
@RequiredArgsConstructor
public class OrderStatusUpdateHandler {

    // Inject the OrderRepository and OrderStatusPublisher dependencies into the handler class using constructor injection.
    private final OrderRepository orderRepository;
    private final OrderStatusPublisher orderStatusPublisher;

    /**
     * Update Order Status Method
     *
     * This method updates the status of a purchase order identified by its ID. It takes an integer ID and a function
     * that performs updates on the order. If the order with the given ID exists, the function is executed to update it.
     * The updateOrder method ensures that all updates to the order are done together, and if any error occurs during
     * the update, the changes are rolled back to keep the data consistent.
     *
     * @param id       The ID of the purchase order to be updated.
     * @param consumer A function that performs updates on the PurchaseOrder.
     *
     * Explanation:
     * The updateOrder method allows you to update the status of a purchase order using its ID.
     * The method takes two arguments:
     *  - id: The ID of the purchase order you want to update. You should provide the unique identifier of the order.
     *  - consumer: This is a special function that you provide, and it will be applied to the PurchaseOrder if it exists.
     *    The consumer function is like a set of instructions that define what changes should be made to the order.
     *    If the order with the given ID is found in the database, the consumer function will be executed on that order.
     *    In simple terms, it's like telling the method, "Hey, if you find the order, here's what you should do with it."
     *    The consumer function is responsible for setting the payment status of the order, and based on that status,
     *    the method will decide whether the order is completed or canceled.
     *    This way, you can easily customize what actions to take based on the payment status of the order.
     *
     * Usage Example:
     * Assume you have a method `updatePaymentStatus` that updates the payment status of a purchase order:
     * ```
     * public void updatePaymentStatus(PurchaseOrder order, PaymentStatus newStatus) {
     *     order.setPaymentStatus(newStatus);
     * }
     * ```
     * Now you can use the `updateOrder` method to update the order's status based on its payment status:
     * ```
     * int orderId = 1234; // Replace with the actual order ID you want to update.
     * updateOrder(orderId, order -> updatePaymentStatus(order, PaymentStatus.PAYMENT_COMPLETED));
     * ```
     * In this example, the `updatePaymentStatus` method is provided as the `Consumer`, and it sets the payment status
     * to `PAYMENT_COMPLETED`. If the payment status is `PAYMENT_COMPLETED`, the order status will be set to `ORDER_COMPLETED`.
     * Otherwise, if the payment status is not `PAYMENT_COMPLETED`, the order status will be set to `ORDER_CANCELLED`.
     * If the payment is not completed, a corresponding order event will be published.
     *
     * Note: The Consumer functional interface is used for performing some processing on an object without returning any result.
     */
    @Transactional
    public void updateOrder(int id, Consumer<PurchaseOrder> consumer) {
        // Find the PurchaseOrder by its ID from the OrderRepository.
        // If the PurchaseOrder exists, apply the Consumer function on it and update the PurchaseOrder.
        orderRepository.findById(id).ifPresent(consumer.andThen(this::updateOrder));
    }
    @Transactional
    public void updateOrderUsingAccept(int id, Consumer<PurchaseOrder> consumer) {
        // Find the PurchaseOrder by its ID from the OrderRepository.
        Optional<PurchaseOrder> optionalOrder = orderRepository.findById(id);
        // If the PurchaseOrder exists, apply the provided Consumer function on it and update the PurchaseOrder.
        if (optionalOrder.isPresent()) {
            PurchaseOrder purchaseOrder = optionalOrder.get();
            consumer.accept(purchaseOrder); // Apply the Consumer function on the PurchaseOrder.
            updateOrder(purchaseOrder); // Call the updateOrder method to handle further processing.
        }
    }


    /**
     * Update Order Method
     *
     * This private method performs the actual update of the PurchaseOrder based on the PaymentStatus.
     * If the PaymentStatus is PAYMENT_COMPLETED, the order status is set to ORDER_COMPLETED.
     * If the PaymentStatus is not PAYMENT_COMPLETED, the order status is set to ORDER_CANCELLED.
     * If the payment is not completed, a corresponding order event is published using the OrderStatusPublisher.
     *
     * @param purchaseOrder The PurchaseOrder object to be updated.
     *
     * Explanation:
     * The updateOrder private method is responsible for updating the status of the purchase order based on its payment status.
     * It takes a PurchaseOrder object as input, which represents the order you want to update.
     * The method checks the payment status of the order, and based on that status, it determines whether the order
     * should be completed or canceled.
     * If the payment status is PAYMENT_COMPLETED, it means the payment has been successfully processed, and the order
     * can be marked as completed.
     * If the payment status is not PAYMENT_COMPLETED, it means there was an issue with the payment, and the order should
     * be canceled.
     * In either case, the method sets the order status accordingly.
     * If the payment is not completed, the method also publishes a corresponding order event using the OrderStatusPublisher.
     * This order event will notify other parts of the application that the order has been canceled due to payment failure.
     */

    private void updateOrder(PurchaseOrder purchaseOrder) {
        // Check if the PaymentStatus is PAYMENT_COMPLETED to determine the order status.
        boolean isPaymentComplete = PaymentStatus.PAYMENT_COMPLETED.equals(purchaseOrder.getPaymentStatus());

        // Set the order status based on the PaymentStatus in the PurchaseOrder.
        OrderStatus orderStatus = isPaymentComplete ? OrderStatus.ORDER_COMPLETED : OrderStatus.ORDER_CANCELLED;
        purchaseOrder.setOrderStatus(orderStatus);

        // If the payment is not completed, publish a corresponding order event using the OrderStatusPublisher.
        if (!isPaymentComplete) {
            OrderRequestDto orderRequestDto = convertPurchaseOrderToOrderRequestDto(purchaseOrder);
            orderStatusPublisher.publishOrderEvent(orderRequestDto, orderStatus);
        }
    }

    /**
     * Convert Entity to DTO Method
     *
     * This method converts a PurchaseOrder entity to an OrderRequestDto.
     *
     * @param purchaseOrder The PurchaseOrder entity to be converted to an OrderRequestDto.
     * @return The created OrderRequestDto object representing the PurchaseOrder information.
     *
     * Explanation:
     * The convertPurchaseOrderToOrderRequestDto method is responsible for converting a PurchaseOrder entity into
     * an OrderRequestDto object.
     * It takes a PurchaseOrder object as input, which contains the information of the purchase order.
     * The method extracts the necessary information from the PurchaseOrder, such as the order ID, user ID,
     * product price, and product ID, and creates a new OrderRequestDto object using this information.
     * The OrderRequestDto is a simpler representation of the PurchaseOrder, and it is useful when publishing
     * order events to other parts of the application.
     * By using the OrderRequestDto, other components can easily understand and process the order information
     * without needing to deal with the complexities of the PurchaseOrder entity.
     */

    public OrderRequestDto convertPurchaseOrderToOrderRequestDto(PurchaseOrder purchaseOrder) {
        return OrderRequestDto.builder()
                .orderId(purchaseOrder.getId())
                .userId(purchaseOrder.getUserId())
                .productPrice(purchaseOrder.getPrice())
                .productId(purchaseOrder.getProductId())
                .build();
    }
}
