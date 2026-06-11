package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * OrderItemRepository - Database access layer for the OrderItem entity.
 *
 * OrderItems are usually managed through the Order entity (via cascade),
 * but this repository is available for direct access if needed.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // No custom methods needed — OrderItems are managed through Order's cascade
}
