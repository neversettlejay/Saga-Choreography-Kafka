/**
 * OrderService is a service class that manages order-related operations and interactions with the database.
 */
package com.jaytech.saga.order.service;

import com.jaytech.genericevent.CustomEvent;
import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.order.entity.PurchaseOrder;
import com.jaytech.saga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    /**
     * The OrderRepository instance to interact with the database for order-related operations.
     */
    private final OrderRepository orderRepository;

    /**
     * The OrderStatusPublisher instance to publish Kafka events related to order status changes.
     */
    private final OrderStatusPublisher orderStatusPublisher;

    /**
     * Create a new purchase order based on the provided OrderRequestDto.
     *
     * @param orderRequestDto The OrderRequestDto containing the necessary information for creating the order.
     * @return The created PurchaseOrder entity representing the new order.
     */
    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        // Step 1: Save the order details in the database
        PurchaseOrder order = saveOrder(orderRequestDto);

        // Step 2: Publish a Kafka event with status ORDER_CREATED
        publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);

        // Return the saved order
        return order;
    }

    /**
     * Saves the order details in the database and updates the DTO with the generated order ID.
     *
     * @param orderRequestDto The order request DTO containing order details
     * @return The saved PurchaseOrder entity
     */
    public PurchaseOrder saveOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder order = orderRepository.save(convertOrderRequestDtoToPurchaseOrder(orderRequestDto));

        // Update the DTO with the generated order ID
        orderRequestDto.setOrderId(order.getId());

        return order;
    }

    public PurchaseOrder createAndProcessOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder = saveOrder(orderRequestDto);
        publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);

        long startTimestamp = System.currentTimeMillis();
        long maxWaitingTimeMillis = 30000;  // Maximum waiting time: 30 seconds

        while (System.currentTimeMillis() - startTimestamp < maxWaitingTimeMillis) {
            PurchaseOrder fetchedOrder = orderRepository.findById(purchaseOrder.getId()).orElse(null);

            if (fetchedOrder != null && fetchedOrder.getOrderStatus() != null && fetchedOrder.getPaymentStatus() != null) {
                return fetchedOrder;
            }

            try {
                Thread.sleep(100); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException("Timed out while waiting for statuses");
    }




    /**
     * Publishes a Kafka event with the provided order status.
     *
     * @param orderRequestDto The order request DTO containing order details
     * @param orderStatus     The status of the order event
     */
    public void publishOrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        orderStatusPublisher.publishOrderEvent(orderRequestDto, orderStatus);
    }



    /**
     * Retrieve all existing purchase orders from the database.
     *
     * @return A List of PurchaseOrder entities representing all the existing orders.
     */
    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Convert the OrderRequestDto to a PurchaseOrder entity.
     *
     * @param dto The OrderRequestDto to be converted.
     * @return The PurchaseOrder entity created from the DTO.
     */
    private PurchaseOrder convertOrderRequestDtoToPurchaseOrder(OrderRequestDto dto) {
        return PurchaseOrder.builder().productId(dto.getProductId()).userId(dto.getUserId()).orderStatus(OrderStatus.ORDER_CREATED).price(dto.getProductPrice()).build();
    }
}
