import { useEffect, useState } from 'react';
import { api } from '../../lib/api';
import { Bell, Check, Trash2 } from 'lucide-react';
import './AdminNotifications.css';

export default function AdminNotifications() {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadNotifications();
    }, []);

    const loadNotifications = async () => {
        try {
            const { data, error } = await api.admin.getNotifications();

            if (error) throw error;
            setNotifications(data || []);
        } catch (error) {
            console.error('Error loading notifications:', error);
        } finally {
            setLoading(false);
        }
    };

    const markAsRead = async (id) => {
        try {
            await api.admin.markNotificationRead(id);
            setNotifications(notifications.map(n => n.id === id ? { ...n, isRead: true } : n));
        } catch (error) {
            console.error('Error marking as read:', error);
        }
    };

    const deleteNotification = async (id) => {
        try {
            await api.admin.deleteNotification(id);
            setNotifications(notifications.filter(n => n.id !== id));
        } catch (error) {
            console.error('Error deleting notification:', error);
        }
    };

    if (loading) return <div>Loading notifications...</div>;

    return (
        <div className="admin-notifications">
            <div className="section-header">
                <h2>System Notifications</h2>
                <p className="text-muted">Alerts and system updates</p>
            </div>

            <div className="notifications-list card">
                {notifications.length === 0 ? (
                    <div className="empty-notifications">
                        <Bell size={48} className="text-muted" />
                        <p>No notifications yet</p>
                    </div>
                ) : (
                    notifications.map(notification => (
                        <div
                            key={notification.id}
                            className={`notification-item ${notification.isRead ? 'read' : 'unread'}`}
                        >
                            <div className="notification-icon">
                                <Bell size={20} />
                            </div>
                            <div className="notification-content">
                                <p className="notification-message">{notification.message}</p>
                                <span className="notification-time">
                                    {new Date(notification.createdAt).toLocaleString()}
                                </span>
                            </div>
                            <div className="notification-actions">
                                {!notification.isRead && (
                                    <button
                                        className="btn-icon"
                                        onClick={() => markAsRead(notification.id)}
                                        title="Mark as read"
                                    >
                                        <Check size={18} />
                                    </button>
                                )}
                                <button
                                    className="btn-icon delete-btn"
                                    onClick={() => deleteNotification(notification.id)}
                                    title="Delete"
                                >
                                    <Trash2 size={18} />
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
