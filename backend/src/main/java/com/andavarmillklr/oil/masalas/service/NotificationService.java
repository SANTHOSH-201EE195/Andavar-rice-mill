package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.model.Notification;
import com.andavarmillklr.oil.masalas.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NotificationService - Manages admin notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Get all notifications (newest first).
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Mark a notification as read.
     */
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Delete a notification.
     */
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
