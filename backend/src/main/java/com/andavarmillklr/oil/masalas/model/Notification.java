package com.andavarmillklr.oil.masalas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification Entity - Represents a system notification for the admin.
 *
 * Notifications are created automatically when certain events happen,
 * such as a new order being placed. The admin can view, mark as read,
 * or delete notifications from the admin panel.
 *
 * Table name: notifications
 *
 * Fields:
 *   - id:        Unique notification identifier
 *   - type:      Type of notification (e.g., "order")
 *   - message:   The notification message text
 *   - isRead:    Whether the admin has seen this notification
 *   - createdAt: When the notification was created
 */
@Entity
@Table(name = "notifications")
public class Notification {

    // ========== Primary Key ==========

    /** Unique ID for this notification */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Notification Content ==========

    /** Type of notification — e.g., "order", "review", "system" */
    @Column(length = 50)
    private String type;

    /** The notification message displayed to the admin */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Whether the admin has read/seen this notification */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    // ========== Timestamps ==========

    /** When this notification was created */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ========== Constructors ==========

    /** Default constructor (required by JPA) */
    public Notification() {
    }

    /**
     * Convenience constructor to quickly create a notification.
     *
     * @param type    The type of notification (e.g., "order")
     * @param message The notification message
     */
    public Notification(String type, String message) {
        this.type = type;
        this.message = message;
        this.isRead = false;
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
