package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem Entity - Represents a single product within an order.
 *
 * When a user places an order, each product in their cart becomes an OrderItem.
 * We store the product name and price at the time of purchase, so even if the
 * product is later deleted or its price changes, the order history remains accurate.
 *
 * Table name: order_items
 *
 * Relationships:
 *   - Many OrderItems belong to one Order (order_id foreign key)
 *   - Many OrderItems reference one Product (product_id foreign key)
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    // ========== Primary Key ==========

    /** Unique ID for this order item */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Relationships ==========

    /** The order this item belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Order order;

    /** Reference to the product (may be null if product was deleted later) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // ========== Snapshot Fields ==========
    // These store the product info AT THE TIME OF PURCHASE
    // so the order history is always accurate

    /** Product name at the time of purchase */
    @Column(name = "product_name")
    private String productName;

    /** Product price at the time of purchase */
    @Column(name = "product_price", precision = 10, scale = 2)
    private BigDecimal productPrice;

    /** Number of this product purchased */
    @Column(nullable = false)
    private Integer quantity;

    // ========== Timestamps ==========

    /** When this order item was created */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public OrderItem() {
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
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
