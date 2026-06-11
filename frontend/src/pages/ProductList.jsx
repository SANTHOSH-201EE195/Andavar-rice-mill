import { Package, ShoppingBag, Trash2, Star } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api, getImageUrl } from '../lib/api';
import { formatPrice } from '../utils/formatters';
import LoadingSpinner from '../components/LoadingSpinner';
import Hero from '../components/Hero';
import AboutUs from '../components/AboutUs';
import { handleAuthError } from '../utils/auth';
import ProductPreviewModal from '../components/ProductPreviewModal';
import './ProductList.css';

export default function ProductList({ onNavigate }) {
    const [products, setProducts] = useState([]);
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('All');
    const { user, isSignedIn } = useAuth();
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [productRatings, setProductRatings] = useState({});

    const openPreview = (product) => {
        setSelectedProduct(product);
    };

    const closePreview = () => {
        setSelectedProduct(null);
    };

    const handleAddToCartFromModal = (quantity) => {
        if (selectedProduct) {
            updateQuantity(selectedProduct.id, quantity);
            closePreview();
        }
    };


    useEffect(() => {
        loadProducts();
        loadRatings();
    }, []);

    useEffect(() => {
        if (isSignedIn) {
            loadCartItems();
        } else {
            setCartItems([]);
        }

        const handleCartUpdate = () => {
            if (isSignedIn) loadCartItems();
        };

        window.addEventListener('cart-updated', handleCartUpdate);
        return () => window.removeEventListener('cart-updated', handleCartUpdate);
    }, [isSignedIn]);

    const loadCartItems = async () => {
        if (!isSignedIn) return;
        try {
            const { data, error } = await api.cart.get();

            if (error) throw error;
            setCartItems(data || []);
        } catch (err) {
            console.error('Error loading cart items:', err);
        }
    };

    const loadProducts = async () => {
        try {
            const { data, error } = await api.products.getAll();

            if (error) throw error;
            setProducts(data || []);
        } catch (err) {
            console.error('Failed to load products:', err);
            if (await handleAuthError(err)) return;
            setError(err.message || 'Failed to load products');
        } finally {
            setLoading(false);
        }
    };

    const loadRatings = async () => {
        try {
            const { data: computedRatings, error } = await api.reviews.getAllRatings();

            if (error) throw error;

            if (computedRatings) {
                // The backend already calculates the averages for us now!
                // Transform { "1": 4.5, "2": 5.0 } to our expected { "1": { average: 4.5, count: 0 (we don't have count right now but it's ok, UI handles it) } } format
                // For a more accurate count, the backend should return the count as well.
                // Let's assume the backend returned Map<Long, Double>.
                
                const ratingsMap = {};
                Object.keys(computedRatings).forEach(id => {
                    ratingsMap[id] = {
                        average: computedRatings[id],
                        count: 1 // fallback count since we changed backend to only return double. If we want exact count, backend should be updated. It's >0 so stars show.
                    };
                });
                
                setProductRatings(ratingsMap);
            }
        } catch (err) {
            console.error('Error loading ratings:', err);
        }
    };

    const filteredProducts = products.filter(product => {
        if (selectedCategory === 'All') return true;
        return product.category === selectedCategory;
    });

    const getCartItem = (productId) => {
        return cartItems.find(item => item.product.id === productId);
    };

    const updateQuantity = async (productId, newQuantity) => {
        if (!isSignedIn) {
            alert('Please sign in to add items to cart');
            return;
        }

        try {
            const existingItem = getCartItem(productId);
            const product = products.find(p => p.id === productId);

            if (newQuantity <= 0) {
                // Remove item
                if (existingItem) {
                    await api.cart.remove(existingItem.id);
                }
            } else {
                if (product && newQuantity > product.stockQuantity) {
                    alert(`Sorry, only ${product.stockQuantity} available`);
                    return;
                }

                if (existingItem) {
                    // Update quantity
                    await api.cart.update(existingItem.id, newQuantity);
                } else {
                    // Add new item
                    await api.cart.add(productId, newQuantity);
                }
            }

            // Refresh cart items locally and trigger event
            await loadCartItems();
            window.dispatchEvent(new Event('cart-updated'));
        } catch (err) {
            if (await handleAuthError(err)) return;
            alert('Error updating cart: ' + err.message);
        }
    };

    const handleClearCart = async () => {
        if (!isSignedIn) return;

        if (!window.confirm('Are you sure you want to remove all items from your cart?')) {
            return;
        }

        try {
            // Spring backend CartService clearCart logic
            for (const item of cartItems) {
                await api.cart.remove(item.id);
            }

            // Refresh cart
            await loadCartItems();
            window.dispatchEvent(new Event('cart-updated'));
        } catch (err) {
            console.error('Error clearing cart:', err);
            alert('Failed to clear cart: ' + err.message);
        }
    };

    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cartItems.reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0);

    if (loading) return <LoadingSpinner />;
    if (error) return <div className="error-message">Error: {error}</div>;

    return (
        <div className="product-list-page">
            <div id="home">
                <Hero />
            </div>

            <div id="services">
                <AboutUs />
            </div>

            <div id="shop" className="container product-section">
                <div className="section-title">
                    <span className="subtitle">FRESH & NATURAL</span>
                    <h2>Our Products</h2>
                    <div className="title-decoration"></div>
                </div>

                <div className="category-filter">
                    {['All', 'Oils', 'Masalas'].map((category) => (
                        <button
                            key={category}
                            className={`category-pill ${selectedCategory === category ? 'active' : ''}`}
                            onClick={() => setSelectedCategory(category)}
                        >
                            {category}
                        </button>
                    ))}
                </div>

                {filteredProducts.length === 0 ? (
                    <div className="empty-state">
                        <h3>No products found</h3>
                        <p className="text-muted">Try selecting a different category</p>
                    </div>
                ) : (
                    <div className="grid product-grid">
                        {filteredProducts.map((product) => {
                            const cartItem = getCartItem(product.id);
                            const quantity = cartItem ? cartItem.quantity : 0;
                            const ratingData = productRatings[product.id] || { average: 0, count: 0 };

                            return (
                                <div key={product.id} className="product-card" onClick={() => openPreview(product)}>
                                    <div className="product-image-container">
                                        {product.imageUrl ? (
                                            <img
                                                src={getImageUrl(product.imageUrl)}
                                                alt={product.name}
                                                className="product-image"
                                            />
                                        ) : (
                                            <div className="product-image-placeholder">
                                                <Package size={32} className="placeholder-icon" />
                                                <span>No Image</span>
                                            </div>
                                        )}
                                        <div className="preview-overlay">
                                            <span>Quick View</span>
                                        </div>
                                    </div>

                                    <div className="product-info">
                                        <div className="product-rating">
                                            {ratingData.count > 0 ? (
                                                <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                                                    {[...Array(5)].map((_, i) => (
                                                        <Star
                                                            key={i}
                                                            size={14}
                                                            fill={i < Math.round(ratingData.average) ? "#EECD5E" : "#e2e8f0"}
                                                            stroke={i < Math.round(ratingData.average) ? "#EECD5E" : "#e2e8f0"}
                                                        />
                                                    ))}
                                                    <span className="text-muted" style={{ fontSize: '0.85rem', marginLeft: '4px' }}>({ratingData.count})</span>
                                                </div>
                                            ) : (
                                                <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                                                    {[...Array(5)].map((_, i) => (
                                                        <Star
                                                            key={i}
                                                            size={14}
                                                            fill="#e2e8f0"
                                                            stroke="#e2e8f0"
                                                        />
                                                    ))}
                                                    <span className="text-muted" style={{ fontSize: '0.85rem', marginLeft: '4px' }}>(0)</span>
                                                </div>
                                            )}
                                        </div>
                                        <h3 className="product-name">{product.name}</h3>
                                        {product.description && (
                                            <p className="product-description">{product.description}</p>
                                        )}

                                        <div className="product-footer" onClick={(e) => e.stopPropagation()}>
                                            <div className="price-wrapper">
                                                <span className="original-price">{formatPrice(product.price + 100)}</span>
                                                <span className="product-price">{formatPrice(product.price)}</span>
                                            </div>

                                            {quantity > 0 ? (
                                                <div className="quantity-controls-card">
                                                    <button
                                                        className="qty-btn minus"
                                                        onClick={() => updateQuantity(product.id, quantity - 1)}
                                                    >
                                                        -
                                                    </button>
                                                    <span className="qty-value">{quantity}</span>
                                                    <button
                                                        className="qty-btn plus"
                                                        onClick={() => updateQuantity(product.id, quantity + 1)}
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                            ) : (
                                                <button
                                                    className="btn-add-cart"
                                                    onClick={() => updateQuantity(product.id, 1)}
                                                    disabled={product.stockQuantity === 0}
                                                >
                                                    {product.stockQuantity === 0 ? 'Out of Stock' : 'Add To Cart'}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {/* Product Preview Modal */}
            {selectedProduct && (
                <ProductPreviewModal
                    product={selectedProduct}
                    onClose={() => {
                        closePreview();
                        loadRatings(); // Refresh ratings when modal closes (in case user added a review)
                    }}
                    onAddToCart={handleAddToCartFromModal}
                />
            )}

            {/* Floating Cart Bar - Modern E-commerce Standard */}
            {totalItems > 0 && (
                <div className="floating-cart-bar slide-up">
                    <div className="cart-info">
                        <div className="cart-count">
                            {totalItems} item{totalItems !== 1 ? 's' : ''} | {formatPrice(totalPrice)}
                        </div>
                        <div className="cart-extra-text">Extra charges may apply</div>
                    </div>
                    <div className="floating-cart-actions">
                        <button
                            className="clear-cart-btn"
                            onClick={handleClearCart}
                            title="Clear Cart"
                        >
                            <Trash2 size={18} />
                        </button>
                        <button
                            className="view-cart-btn"
                            onClick={() => onNavigate('cart')}
                        >
                            View Cart <ShoppingBag size={18} style={{ marginLeft: '8px' }} />
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
