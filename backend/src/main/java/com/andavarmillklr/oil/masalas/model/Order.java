package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Represents a completed purchase by a user.
 *
 * When a user checks out their cart, an Order is created with all the
 * shipping details and a list of OrderItems (the products they bought).
 *
 * Table name: orders
 *
 * Relationships:
 *   - Many Orders belong to one User (user_id foreign key)
 *   - One Order has many OrderItems (order_items table)
 *
 * Status flow: pending → processing → shipped → delivered
 *              pending → cancelled (if the order is cancelled)
 */
@Entity
@Table(name = "orders")
public class Order {

    // ========== Primary Key ==========

    /** Unique ID for this order */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== User Reference ==========

    /** The user who placed this order */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // ========== Order Details ==========

    /** Total amount of the order in INR */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Current status of the order.
     * Possible values: "pending", "processing", "shipped", "delivered", "cancelled"
     */
    @Column(length = 20)
    private String status = "pending";

    // ========== Shipping Information ==========

    /** Name of the person receiving the order */
    @Column(name = "shipping_name", length = 100)
    private String shippingName;

    /** Email address for order confirmations */
    @Column(name = "shipping_email", length = 100)
    private String shippingEmail;

    /** Street address for delivery */
    @Column(name = "shipping_address")
    private String shippingAddress;

    /** City for delivery */
    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    /** Postal/ZIP code for delivery */
    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    /** Country for delivery */
    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    // ========== Order Items (One-to-Many Relationship) ==========

    /**
     * List of items in this order.
     * CascadeType.ALL means when we save/delete an Order, all its OrderItems
     * are also saved/deleted automatically.
     * orphanRemoval = true means if we remove an OrderItem from this list,
     * it gets deleted from the database too.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // ========== Timestamps ==========

    /** When this order was placed */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public Order() {
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingEmail() {
        return shippingEmail;
    }

    public void setShippingEmail(String shippingEmail) {
        this.shippingEmail = shippingEmail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
