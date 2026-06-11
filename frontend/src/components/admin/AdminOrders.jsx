import { useEffect, useState } from 'react';
import { api } from '../../lib/api';

import { formatPrice } from '../../utils/formatters';
import { Eye } from 'lucide-react';
import './AdminOrders.css';

export default function AdminOrders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedOrder, setSelectedOrder] = useState(null);

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            const { data, error } = await api.admin.getAllOrders();

            if (error) throw error;
            setOrders(data || []);
        } catch (error) {
            console.error('Error loading orders:', error);
        } finally {
            setLoading(false);
        }
    };

    const updateStatus = async (orderId, newStatus) => {
        try {
            const { error } = await api.admin.updateOrderStatus(orderId, newStatus);

            if (error) throw error;

            // Create notification for user (optional, future enhancement)

            // Optimistic update
            setOrders(orders.map(o => o.id === orderId ? { ...o, status: newStatus } : o));
            if (selectedOrder?.id === orderId) {
                setSelectedOrder({ ...selectedOrder, status: newStatus });
            }
            alert('Status updated successfully');
        } catch (error) {
            console.error('Error updating status:', error);
            alert('Failed to update status');
        }
    };

    if (loading) return <div>Loading orders...</div>;

    return (
        <div className="admin-orders">
            <div className="section-header">
                <h2>Order Management</h2>
                <p className="text-muted">Track and update customer orders</p>
            </div>

            <div className="orders-table-wrapper card">
                <table>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Customer</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map(order => (
                            <tr key={order.id}>
                                <td className="font-mono">{order.id}</td>
                                <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                                <td>{order.shippingName}</td>
                                <td className="font-bold">{formatPrice(order.totalAmount)}</td>
                                <td>
                                    <select
                                        value={order.status}
                                        onChange={(e) => updateStatus(order.id, e.target.value)}
                                        className={`status-select status-${order.status.toLowerCase()}`}
                                    >
                                        <option value="pending">Pending</option>
                                        <option value="processing">Processing</option>
                                        <option value="shipped">Shipped</option>
                                        <option value="delivered">Delivered</option>
                                        <option value="cancelled">Cancelled</option>
                                    </select>
                                </td>
                                <td>
                                    <button
                                        className="btn-icon"
                                        onClick={() => setSelectedOrder(order)}
                                        title="View Details"
                                    >
                                        <Eye size={18} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {orders.length === 0 && (
                    <div className="empty-table-state">No orders found</div>
                )}
            </div>

            {selectedOrder && (
                <div className="modal-overlay" onClick={() => setSelectedOrder(null)}>
                    <div className="modal-content card" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Order Details #{selectedOrder.id}</h3>
                            <button className="modal-close" onClick={() => setSelectedOrder(null)}>×</button>
                        </div>

                        <div className="order-details-grid">
                            <div className="detail-group">
                                <h4>Shipping Info</h4>
                                <p><strong>{selectedOrder.shippingName}</strong></p>
                                <p>{selectedOrder.shippingEmail}</p>
                                <p>{selectedOrder.shippingAddress}</p>
                                <p>{selectedOrder.shippingCity}, {selectedOrder.shippingPostalCode}</p>
                                <p>{selectedOrder.shippingCountry}</p>
                            </div>

                            <div className="detail-group">
                                <h4>Order Items</h4>
                                <ul className="admin-items-list">
                                    {selectedOrder.orderItems?.map(item => (
                                        <li key={item.id} className="admin-item">
                                            <span>{item.productName} <span className="text-muted">x{item.quantity}</span></span>
                                            <span>{formatPrice(item.productPrice * item.quantity)}</span>
                                        </li>
                                    ))}
                                </ul>
                                <div className="admin-order-total">
                                    <span>Total Amount</span>
                                    <span>{formatPrice(selectedOrder.totalAmount)}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
