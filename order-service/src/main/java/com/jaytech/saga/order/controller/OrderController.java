/**
 * OrderController is a REST controller responsible for handling order-related HTTP requests.
 */
package com.jaytech.saga.order.controller;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.order.entity.PurchaseOrder;
import com.jaytech.saga.order.repository.OrderRepository;
import com.jaytech.saga.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    /**
     * The OrderService instance to manage order-related business logic.
     */
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * Create a new purchase order based on the provided OrderRequestDto.
     *
     * @param orderRequestDto The OrderRequestDto containing the necessary information for creating the order.
     * @return The created PurchaseOrder entity representing the new order.
     */
    @PostMapping("/create")
    @SneakyThrows
    public PurchaseOrder createOrder(@RequestBody OrderRequestDto orderRequestDto, @RequestParam boolean getStatus) {
        if (!getStatus) {
            return orderService.createOrder(orderRequestDto);
        } else {
            return orderService.createAndProcessOrder(orderRequestDto);
        }
    }

    /**
     * Retrieve all existing purchase orders.
     *
     * @return A List of PurchaseOrder entities representing all the existing orders.
     */
    @GetMapping("/all")
    public List<PurchaseOrder> getOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Retrieve purchase orders.
     *
     * @return PurchaseOrder entity representing a particular order.
     */
    @GetMapping("/fetch")
    public PurchaseOrder getOrderById(@RequestParam int orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order ID not found"));
    }


}
