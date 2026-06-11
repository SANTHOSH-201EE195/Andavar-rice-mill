import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';
import { formatPrice } from '../utils/formatters';
import LoadingSpinner from '../components/LoadingSpinner';
import './Checkout.css';

export default function Checkout({ onNavigate }) {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const { user, isSignedIn } = useAuth();
    const [formData, setFormData] = useState({
        shippingName: '',
        shippingEmail: '',
        shippingAddress: '',
        shippingCity: '',
        shippingPostalCode: '',
        shippingCountry: '',
    });

    useEffect(() => {
        loadCart();
    }, [isSignedIn]);

    const loadCart = async () => {
        try {
            if (!isSignedIn) {
                onNavigate('products');
                return;
            }

            const { data, error } = await api.cart.get();

            if (error) throw error;

            if (!data || data.length === 0) {
                alert('Your cart is empty');
                onNavigate('cart');
                return;
            }

            setCartItems(data);
            
            // Pre-fill user data
            setFormData(prev => ({
                ...prev,
                shippingName: user?.fullName || '',
                shippingEmail: user?.email || ''
            }));
            
        } catch (err) {
            alert('Error loading cart: ' + err.message);
            onNavigate('cart');
        } finally {
            setLoading(false);
        }
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => {
            return total + (item.product?.price || 0) * item.quantity;
        }, 0);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);

        try {
            if (!isSignedIn) throw new Error('Not authenticated');

            // Spring backend handles moving cart items to order items and clearing the cart
            const { data: order, error } = await api.orders.placeOrder(formData);

            if (error) throw error;

            alert('Order placed successfully! Order ID: ' + order.id);
            window.dispatchEvent(new Event('cart-updated'));
            onNavigate('order-success');
        } catch (err) {
            alert('Error placing order: ' + err.message);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="checkout-page">
            <div className="container">
                <div className="page-header">
                    <h2>Checkout</h2>
                    <p className="text-muted">Complete your order</p>
                </div>

                <div className="checkout-content">
                    <form className="checkout-form card" onSubmit={handleSubmit}>
                        <h3>Shipping Information</h3>

                        <div className="form-group">
                            <label className="form-label">Full Name *</label>
                            <input
                                type="text"
                                className="form-input"
                                value={formData.shippingName}
                                onChange={(e) => setFormData({ ...formData, shippingName: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Email *</label>
                            <input
                                type="email"
                                className="form-input"
                                value={formData.shippingEmail}
                                onChange={(e) => setFormData({ ...formData, shippingEmail: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Address *</label>
                            <input
                                type="text"
                                className="form-input"
                                value={formData.shippingAddress}
                                onChange={(e) => setFormData({ ...formData, shippingAddress: e.target.value })}
                                required
                            />
                        </div>

                        <div className="grid grid-2">
                            <div className="form-group">
                                <label className="form-label">City *</label>
                                <input
                                    type="text"
                                    className="form-input"
                                    value={formData.shippingCity}
                                    onChange={(e) => setFormData({ ...formData, shippingCity: e.target.value })}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Postal Code *</label>
                                <input
                                    type="text"
                                    className="form-input"
                                    value={formData.shippingPostalCode}
                                    onChange={(e) => setFormData({ ...formData, shippingPostalCode: e.target.value })}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Country *</label>
                            <input
                                type="text"
                                className="form-input"
                                value={formData.shippingCountry}
                                onChange={(e) => setFormData({ ...formData, shippingCountry: e.target.value })}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary"
                            style={{ width: '100%' }}
                            disabled={submitting}
                        >
                            {submitting ? 'Processing...' : 'Place Order'}
                        </button>
                    </form>

                    <div className="order-summary card card-glass">
                        <h3>Order Summary</h3>

                        <div className="summary-items">
                            {cartItems.map((item) => (
                                <div key={item.id} className="summary-item">
                                    <div className="summary-item-info">
                                        <span className="summary-item-name">{item.product?.name}</span>
                                        <span className="summary-item-qty">× {item.quantity}</span>
                                    </div>
                                    <span className="summary-item-price">
                                        {formatPrice((item.product?.price || 0) * item.quantity)}
                                    </span>
                                </div>
                            ))}
                        </div>

                        <div className="summary-divider"></div>

                        <div className="summary-row">
                            <span>Subtotal</span>
                            <span>{formatPrice(calculateTotal())}</span>
                        </div>
                        <div className="summary-row">
                            <span>Shipping</span>
                            <span>Free</span>
                        </div>
                        <div className="summary-divider"></div>
                        <div className="summary-row total">
                            <span>Total</span>
                            <span>{formatPrice(calculateTotal())}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
