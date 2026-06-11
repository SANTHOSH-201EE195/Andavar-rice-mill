package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * CartItemRepository - Database access layer for the CartItem entity.
 *
 * Provides methods to manage a user's shopping cart — finding items,
 * checking for existing items, and clearing the cart during checkout.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Get all cart items for a specific user.
     * Used to display the user's cart page and the floating cart bar.
     *
     * @param userId The ID of the user whose cart we want
     * @return List of cart items with their associated products
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Find a specific cart item for a user and product combination.
     * Used to check if a product is already in the cart (so we update
     * the quantity instead of adding a duplicate entry).
     *
     * @param userId    The user's ID
     * @param productId The product's ID
     * @return Optional containing the CartItem if found
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Delete all cart items for a specific user.
     * Used during checkout to clear the cart after a successful order.
     *
     * @param userId The ID of the user whose cart to clear
     */
    void deleteByUserId(Long userId);

    /**
     * Count how many items are in a user's cart.
     * Used to display the cart badge count in the header.
     *
     * @param userId The user's ID
     * @return Number of items in the user's cart
     */
    long countByUserId(Long userId);
}
