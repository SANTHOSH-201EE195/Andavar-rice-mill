import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api, getImageUrl } from '../lib/api';
import { formatPrice } from '../utils/formatters';
import LoadingSpinner from '../components/LoadingSpinner';
import './Cart.css';

export default function Cart({ onNavigate }) {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { user, isSignedIn, signOut } = useAuth();

    useEffect(() => {
        loadCart();

        // Listen for cart updates
        const handleCartUpdate = () => loadCart();
        window.addEventListener('cart-updated', handleCartUpdate);
        return () => window.removeEventListener('cart-updated', handleCartUpdate);
    }, [isSignedIn]);

    const loadCart = async () => {
        try {
            if (!isSignedIn) {
                setCartItems([]);
                setLoading(false);
                return;
            }

            const { data, error } = await api.cart.get();

            if (error) throw error;
            setCartItems(data || []);
        } catch (err) {
            if (err.message && (err.message.includes('Session expired') || err.message.includes('jwt expired'))) {
                signOut();
                window.location.reload();
                return;
            }
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const updateQuantity = async (itemId, newQuantity) => {
        if (newQuantity < 1) return;

        try {
            await api.cart.update(itemId, newQuantity);
            loadCart();
        } catch (err) {
            alert('Error updating quantity: ' + err.message);
        }
    };

    const removeItem = async (itemId) => {
        try {
            await api.cart.remove(itemId);
            loadCart();
            window.dispatchEvent(new Event('cart-updated'));
        } catch (err) {
            alert('Error removing item: ' + err.message);
        }
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => {
            return total + (item.product?.price || 0) * item.quantity;
        }, 0);
    };

    if (loading) return <LoadingSpinner />;
    if (error) return <div className="error-message">Error: {error}</div>;

    return (
        <div className="cart-page">
            <div className="container">
                <div className="page-header">
                    <h2>Shopping Cart</h2>
                    <p className="text-muted">Review your items before checkout</p>
                </div>

                {cartItems.length === 0 ? (
                    <div className="empty-state">
                        <h3>Your cart is empty</h3>
                        <p className="text-muted">Add some products to get started!</p>
                        <button className="btn btn-primary" onClick={() => onNavigate('products')}>
                            Browse Products
                        </button>
                    </div>
                ) : (
                    <div className="cart-content">
                        <div className="cart-items">
                            {cartItems.map((item) => (
                                <div key={item.id} className="cart-item card slide-in">
                                    <div className="cart-item-image">
                                        {item.product?.imageUrl ? (
                                            <img src={getImageUrl(item.product.imageUrl)} alt={item.product.name} />
                                        ) : (
                                            <div className="image-placeholder">No Image</div>
                                        )}
                                    </div>

                                    <div className="cart-item-details">
                                        <h3>{item.product?.name}</h3>
                                        {item.product?.description && (
                                            <p className="text-muted text-small">{item.product.description}</p>
                                        )}
                                        <p className="cart-item-price">{formatPrice(item.product?.price || 0)}</p>
                                    </div>

                                    <div className="cart-item-actions">
                                        <div className="quantity-controls">
                                            <button
                                                className="btn btn-secondary btn-sm"
                                                onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                                disabled={item.quantity <= 1}
                                            >
                                                −
                                            </button>
                                            <span className="quantity">{item.quantity}</span>
                                            <button
                                                className="btn btn-secondary btn-sm"
                                                onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                            >
                                                +
                                            </button>
                                        </div>

                                        <p className="item-total">
                                            {formatPrice((item.product?.price || 0) * item.quantity)}
                                        </p>

                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={() => removeItem(item.id)}
                                        >
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="cart-summary card card-glass">
                            <h3>Order Summary</h3>
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
                            <button
                                className="btn btn-primary"
                                style={{ width: '100%', marginTop: '1.5rem' }}
                                onClick={() => onNavigate('checkout')}
                            >
                                Proceed to Checkout
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
