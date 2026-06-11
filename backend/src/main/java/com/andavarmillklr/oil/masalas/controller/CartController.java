package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.model.CartItem;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CartController - API for shopping cart operations.
 *
 * All endpoints require authentication.
 * We use @AuthenticationPrincipal User user to automatically inject
 * the currently logged-in user from the JWT token.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCartItems(user.getId()));
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> requestBody) {
        
        Long productId = Long.valueOf(requestBody.get("productId").toString());
        int quantity = requestBody.containsKey("quantity") ? 
                Integer.parseInt(requestBody.get("quantity").toString()) : 1;
                
        return ResponseEntity.ok(cartService.addToCart(user.getId(), productId, quantity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Integer> requestBody) {
            
        return ResponseEntity.ok(cartService.updateQuantity(id, user.getId(), requestBody.get("quantity")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
            
        cartService.removeFromCart(id, user.getId());
        return ResponseEntity.ok().build();
    }
}
