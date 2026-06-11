package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Entity - Represents an item available for sale in the Andavar store.
 *
 * Each product belongs to a category (either "Oils" or "Masalas") and has
 * a name, description, price, stock quantity, and an optional image.
 *
 * Table name: products
 *
 * Fields:
 *   - id:             Unique product identifier (auto-generated)
 *   - name:           Product name (e.g., "Coconut Oil", "Sambar Powder")
 *   - description:    Detailed description of the product
 *   - price:          Price in INR (Indian Rupees)
 *   - stockQuantity:  Number of items currently in stock
 *   - category:       Category — either "Oils" or "Masalas"
 *   - imageUrl:       URL path to the product image
 *   - imageKey:       Storage key for image management
 *   - createdBy:      ID of the admin who created this product
 *   - createdAt:      Timestamp when the product was added
 */
@Entity
@Table(name = "products")
public class Product {

    // ========== Primary Key ==========

    /** Unique ID for this product */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Product Details ==========

    /** Name of the product (e.g., "Cold Pressed Coconut Oil") */
    @Column(nullable = false)
    private String name;

    /** Detailed description of the product */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Price in INR. We use BigDecimal for accurate currency calculations. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** How many items are currently available in stock */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    /** Product category — "Oils" or "Masalas" */
    @Column(length = 50)
    private String category;

    // ========== Image Fields ==========

    /** URL to access the product image (e.g., "/images/oil/coconut_oil.png") */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Storage key for the image file (used when deleting/replacing images) */
    @Column(name = "image_key", length = 500)
    private String imageKey;

    // ========== Metadata ==========

    /** ID of the admin user who created this product */
    @Column(name = "created_by")
    private Long createdBy;

    /** Timestamp when this product was first added to the database */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Automatically set the creation timestamp before saving for the first time.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public Product() {
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
