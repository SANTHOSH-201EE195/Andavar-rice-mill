package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.model.CartItem;
import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.repository.CartItemRepository;
import com.andavarmillklr.oil.masalas.repository.ProductRepository;
import com.andavarmillklr.oil.masalas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CartService - Manages user shopping carts.
 */
@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all items in a user's cart.
     */
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * Add a product to the user's cart.
     * If it's already there, increase the quantity.
     */
    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // Check if item is already in cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        }

        // It's a new item, so create it
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem newItem = new CartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        
        return cartItemRepository.save(newItem);
    }

    /**
     * Update the quantity of an existing cart item.
     */
    public CartItem updateQuantity(Long cartItemId, Long userId, int newQuantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Make sure the item belongs to the user requesting the change
        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to modify this cart item");
        }

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
            return null; // Item was removed
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    /**
     * Remove an item from the cart.
     */
    public void removeFromCart(Long cartItemId, Long userId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this cart item");
        }

        cartItemRepository.delete(item);
    }

    /**
     * Clear the entire cart for a user.
     */
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
