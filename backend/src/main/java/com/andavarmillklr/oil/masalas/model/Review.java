package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Review Entity - Represents a customer review/rating for a product.
 *
 * Users can leave a star rating (1-5) and an optional comment for any product.
 * Reviews are displayed on the product preview modal and the average rating
 * is shown on each product card.
 *
 * Table name: reviews
 *
 * Relationships:
 *   - Many Reviews belong to one Product (product_id foreign key)
 *   - Many Reviews belong to one User (user_id foreign key)
 */
@Entity
@Table(name = "reviews")
public class Review {

    // ========== Primary Key ==========

    /** Unique ID for this review */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Relationships ==========

    /** The product being reviewed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** The user who wrote this review */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // ========== Review Content ==========

    /** Star rating from 1 (worst) to 5 (best) */
    @Column(nullable = false)
    private Integer rating;

    /** The review text / comment */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** Display name of the reviewer (stored separately for quick access) */
    @Column(name = "user_name", length = 100)
    private String userName;

    // ========== Timestamps ==========

    /** When this review was posted */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public Review() {
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
