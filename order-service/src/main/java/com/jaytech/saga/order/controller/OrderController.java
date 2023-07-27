/**
 * OrderController is a REST controller responsible for handling order-related HTTP requests.
 */
package com.jaytech.saga.order.controller;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.order.entity.PurchaseOrder;
import com.jaytech.saga.order.service.OrderService;
import lombok.RequiredArgsConstructor;
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

    /**
     * Create a new purchase order based on the provided OrderRequestDto.
     *
     * @param orderRequestDto The OrderRequestDto containing the necessary information for creating the order.
     * @return The created PurchaseOrder entity representing the new order.
     */
    @PostMapping("/create")
    public PurchaseOrder createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return orderService.createOrder(orderRequestDto);
    }

    /**
     * Retrieve all existing purchase orders.
     *
     * @return A List of PurchaseOrder entities representing all the existing orders.
     */
    @GetMapping
    public List<PurchaseOrder> getOrders() {
        return orderService.getAllOrders();
    }
}
