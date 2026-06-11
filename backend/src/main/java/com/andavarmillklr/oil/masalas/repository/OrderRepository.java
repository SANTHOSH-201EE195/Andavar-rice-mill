package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * OrderRepository - Database access layer for the Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Get all orders for a specific user, newest first.
     * Used on the "My Orders" page.
     *
     * @param userId The user's ID
     * @return List of orders with order items, newest first
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Get ALL orders (for admin), newest first.
     * Used on the admin "Order Management" page.
     *
     * @return List of all orders, newest first
     */
    List<Order> findAllByOrderByCreatedAtDesc();
}
