import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';
import { formatPrice } from '../utils/formatters';
import LoadingSpinner from '../components/LoadingSpinner';
import { ChevronDown, ChevronUp, Package } from 'lucide-react';
import './MyOrders.css';

export default function MyOrders({ onNavigate }) {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const { isSignedIn } = useAuth();

    useEffect(() => {
        loadOrders();
    }, [isSignedIn]);

    const loadOrders = async () => {
        if (!isSignedIn) {
            onNavigate('products');
            return;
        }

        try {
            const { data, error } = await api.orders.getMyOrders();

            if (error) throw error;

            setOrders(data || []);
        } catch (err) {
            console.error('Error loading orders:', err);
        } finally {
            setLoading(false);
        }
    };

    const toggleOrder = (orderId) => {
        setOrders(orders.map(order =>
            order.id === orderId ? { ...order, expanded: !order.expanded } : order
        ));
    };

    if (loading) return <LoadingSpinner />;

    if (orders.length === 0) {
        return (
            <div className="my-orders-page">
                <div className="container">
                    <div className="empty-state">
                        <Package size={48} className="placeholder-icon" />
                        <h3>No orders yet</h3>
                        <p className="text-muted">You haven't placed any orders yet.</p>
                        <button
                            className="btn-add-cart"
                            style={{ marginTop: '1rem', backgroundColor: '#111a15', color: 'white' }}
                            onClick={() => onNavigate('products')}
                        >
                            Start Shopping
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="my-orders-page">
            <div className="container orders-container">
                <h2 className="page-title">My Orders</h2>

                <div className="orders-list">
                    {orders.map((order) => (
                        <div key={order.id} className="order-card">
                            <div className="order-header" onClick={() => toggleOrder(order.id)}>
                                <div className="order-info">
                                    <h3>Order #{order.id}</h3>
                                    <span className="order-date">
                                        {new Date(order.createdAt).toLocaleDateString()}
                                    </span>
                                </div>
                                <div className="order-meta">
                                    <span className="order-total">{formatPrice(order.totalAmount)}</span>
                                    <span className={`order-status status-${order.status.toLowerCase()}`}>
                                        {order.status}
                                    </span>
                                </div>
                                {order.expanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                            </div>

                            {order.expanded && (
                                <div className="order-details">
                                    <ul className="order-items-list">
                                        {order.orderItems?.map((item) => (
                                            <li key={item.id} className="order-item">
                                                <div className="item-info">
                                                    <span className="item-name">{item.productName}</span>
                                                    <span className="item-qty">x {item.quantity}</span>
                                                </div>
                                                <span className="item-price">
                                                    {formatPrice(item.productPrice * item.quantity)}
                                                </span>
                                            </li>
                                        ))}
                                    </ul>

                                    <div className="shipping-address">
                                        <strong>Shipping Address:</strong>
                                        {order.shippingAddress}, {order.shippingCity}, {order.shippingPostalCode}, {order.shippingCountry}
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
