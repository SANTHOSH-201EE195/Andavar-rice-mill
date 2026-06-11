package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.model.Notification;
import com.andavarmillklr.oil.masalas.model.Order;
import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.service.ImageUploadService;
import com.andavarmillklr.oil.masalas.service.NotificationService;
import com.andavarmillklr.oil.masalas.service.OrderService;
import com.andavarmillklr.oil.masalas.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * AdminController - API for admin panel operations.
 *
 * All endpoints require "ROLE_ADMIN" authority (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final ImageUploadService imageUploadService;

    public AdminController(ProductService productService,
                           OrderService orderService,
                           NotificationService notificationService,
                           ImageUploadService imageUploadService) {
        this.productService = productService;
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.imageUploadService = imageUploadService;
    }

    // ==========================================
    // PRODUCT MANAGEMENT
    // ==========================================

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(
            @AuthenticationPrincipal User admin,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product, admin.getId()));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Upload an image for a product.
     */
    @PostMapping("/products/{id}/image")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            
            // Save the image URL to the product
            Product existingProduct = productService.getProductById(id);
            existingProduct.setImageUrl(imageUrl);
            productService.updateProduct(id, existingProduct);
            
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // ORDER MANAGEMENT
    // ==========================================

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.get("status")));
    }

    // ==========================================
    // NOTIFICATION MANAGEMENT
    // ==========================================

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
