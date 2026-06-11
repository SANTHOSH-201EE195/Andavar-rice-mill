package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * CartItem Entity - Represents a single item in a user's shopping cart.
 *
 * Each cart item links a user to a product with a specific quantity.
 * When the user checks out, these items are converted into order items
 * and then deleted from the cart.
 *
 * Table name: cart_items
 *
 * Relationships:
 *   - Many CartItems belong to one User  (user_id foreign key)
 *   - Many CartItems reference one Product (product_id foreign key)
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    // ========== Primary Key ==========

    /** Unique ID for this cart item */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Relationships ==========

    /**
     * The user who owns this cart item.
     * LAZY loading means the User object is only loaded when we actually need it.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    /**
     * The product that was added to the cart.
     * EAGER loading means the Product data is always loaded with the CartItem,
     * because we almost always need product details (name, price, image) when
     * showing the cart.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ========== Cart Details ==========

    /** How many of this product the user wants to buy */
    @Column(nullable = false)
    private Integer quantity = 1;

    // ========== Timestamps ==========

    /** When this item was added to the cart */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public CartItem() {
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
