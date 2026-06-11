package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * NotificationRepository - Database access layer for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get all notifications sorted by creation date (newest first).
     * Used on the admin notifications page.
     *
     * @return List of all notifications, newest first
     */
    List<Notification> findAllByOrderByCreatedAtDesc();

    /**
     * Count unread notifications.
     * Used to show the notification badge count in the admin sidebar.
     *
     * @param isRead false to count unread notifications
     * @return Number of unread notifications
     */
    long countByIsRead(Boolean isRead);
}
