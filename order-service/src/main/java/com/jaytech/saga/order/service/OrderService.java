/**
 * OrderService is a service class that manages order-related operations and interactions with the database.
 */
package com.jaytech.saga.order.service;

import com.google.common.collect.Maps;
import com.jaytech.genericevent.CustomEvent;
import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.commons.event.PaymentStatus;
import com.jaytech.saga.order.entity.PurchaseOrder;
import com.jaytech.saga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements ApplicationListener<CustomEvent> {

    private Map<String, String> eventData = new HashMap<>();
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
        // Save the order details in the database
        PurchaseOrder order = orderRepository.save(convertOrderRequestDtoToPurchaseOrder(orderRequestDto));

        // Set the generated order ID in the DTO
        orderRequestDto.setOrderId(order.getId());

        // Produce a Kafka event with status ORDER_CREATED
        orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);


        order.setOrderStatus(OrderStatus.ORDER_STATUS_UNKNOWN);
        order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_UNKNOWN);

        eventData.put("UserId", order.getUserId().toString());
        // order.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETED.name().equals(eventData.get("PaymentStatus")) ? PaymentStatus.PAYMENT_COMPLETED : PaymentStatus.PAYMENT_FAILED.name().equals(eventData.get("PaymentStatus")) ? PaymentStatus.PAYMENT_FAILED : PaymentStatus.PAYMENT_STATUS_UNKNOWN);
        // eventData= Maps.newHashMap();

        return order;
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

    @Override
    public void onApplicationEvent(CustomEvent event) {
        eventData.putAll(event.getEventData());
        if (!eventData.isEmpty() && Objects.nonNull(eventData.get("OrderId")))
            log.info("For User '" + eventData.get("UserId") + "' ,OrderID: " + eventData.get("OrderId") + " had payment status " + eventData.get("PaymentStatus"));
//        eventData= Maps.newHashMap();
    }
}
