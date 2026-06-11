import { LayoutDashboard, ShoppingBag, Bell } from 'lucide-react';
import './AdminSidebar.css';

export default function AdminSidebar({ activeTab, onTabChange, notificationCount = 0 }) {
    return (
        <div className="admin-sidebar">
            <div className="sidebar-header">
                <h3>Admin Panel</h3>
            </div>

            <nav className="sidebar-nav">
                <button
                    className={`sidebar-link ${activeTab === 'products' ? 'active' : ''}`}
                    onClick={() => onTabChange('products')}
                >
                    <LayoutDashboard size={20} />
                    <span>Products</span>
                </button>

                <button
                    className={`sidebar-link ${activeTab === 'orders' ? 'active' : ''}`}
                    onClick={() => onTabChange('orders')}
                >
                    <ShoppingBag size={20} />
                    <span>Orders</span>
                </button>

                <button
                    className={`sidebar-link ${activeTab === 'notifications' ? 'active' : ''}`}
                    onClick={() => onTabChange('notifications')}
                >
                    <Bell size={20} />
                    <span>Notifications</span>
                    {notificationCount > 0 && <span className="notification-badge">{notificationCount}</span>}
                </button>
            </nav>
        </div>
    );
}
