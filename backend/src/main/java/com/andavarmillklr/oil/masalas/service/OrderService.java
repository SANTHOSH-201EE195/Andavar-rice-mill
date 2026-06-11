package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.model.*;
import com.andavarmillklr.oil.masalas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderService - Handles order placement and management.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository,
                        NotificationRepository notificationRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Place a new order using the items currently in the user's cart.
     */
    @Transactional
    public Order placeOrder(Long userId, Order shippingDetails) {
        // 1. Get the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get the items in their cart
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Set up the new order
        Order order = new Order();
        order.setUser(user);
        order.setShippingName(shippingDetails.getShippingName());
        order.setShippingEmail(shippingDetails.getShippingEmail());
        order.setShippingAddress(shippingDetails.getShippingAddress());
        order.setShippingCity(shippingDetails.getShippingCity());
        order.setShippingPostalCode(shippingDetails.getShippingPostalCode());
        order.setShippingCountry(shippingDetails.getShippingCountry());
        order.setStatus("pending");

        // 4. Convert cart items into order items and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            
            // Snapshot the product details at the time of purchase
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            order.getOrderItems().add(orderItem);

            // Add to total
            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        // 5. Save the order (CascadeType.ALL will also save the OrderItems)
        Order savedOrder = orderRepository.save(order);

        // 6. Clear the user's cart
        cartItemRepository.deleteByUserId(userId);

        // 7. Create a notification for the admin
        String adminMsg = String.format("New order #%d placed by %s (₹%s)",
                savedOrder.getId(), savedOrder.getShippingName(), savedOrder.getTotalAmount());
        notificationRepository.save(new Notification("order", adminMsg));

        return savedOrder;
    }

    /**
     * Get all orders for a specific user.
     */
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get ALL orders (Admin only).
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Update order status (Admin only).
     */
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
