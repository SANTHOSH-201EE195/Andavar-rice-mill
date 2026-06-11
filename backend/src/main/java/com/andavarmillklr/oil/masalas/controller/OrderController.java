package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.model.Order;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController - API for placing and viewing orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get all orders for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrders(user.getId()));
    }

    /**
     * Place a new order using the current cart.
     */
    @PostMapping
    public ResponseEntity<Order> placeOrder(
            @AuthenticationPrincipal User user,
            @RequestBody Order shippingDetails) {
            
        try {
            Order newOrder = orderService.placeOrder(user.getId(), shippingDetails);
            return ResponseEntity.ok(newOrder);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
