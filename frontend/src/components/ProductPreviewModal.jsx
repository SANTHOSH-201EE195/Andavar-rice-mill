import { useState, useEffect } from 'react';
import { X, Star, Send, ShoppingCart, CheckCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';

import { formatPrice, formatDate } from '../utils/formatters';
import { handleAuthError } from '../utils/auth';
import './ProductPreviewModal.css';

export default function ProductPreviewModal({ product, onClose, onAddToCart }) {
    const { user, isSignedIn } = useAuth();
    const [reviews, setReviews] = useState([]);
    const [loadingReviews, setLoadingReviews] = useState(true);
    const [newRating, setNewRating] = useState(5);
    const [newComment, setNewComment] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [quantity, setQuantity] = useState(1);
    const [averageRating, setAverageRating] = useState(0);

    // Fetch reviews
    useEffect(() => {
        const fetchReviews = async () => {
            try {
                const { data, error } = await api.reviews.getByProduct(product.id);

                if (error) throw error;

                const reviewsData = data || [];
                setReviews(reviewsData);

                // Calculate average rating
                if (reviewsData.length > 0) {
                    const total = reviewsData.reduce((sum, review) => sum + review.rating, 0);
                    setAverageRating(Math.round((total / reviewsData.length) * 10) / 10);
                } else {
                    setAverageRating(0); // Set explicit default
                }
            } catch (err) {
                console.error('Error fetching reviews:', err);
                // Silent fail for non-critical reviews
            } finally {
                setLoadingReviews(false);
            }
        };

        fetchReviews();
        // Prevent body scroll when modal is open
        document.body.style.overflow = 'hidden';
        return () => {
            document.body.style.overflow = 'unset';
            document.body.style.paddingRight = '0px';
        };
    }, [product.id]);

    const handleSubmitReview = async (e) => {
        e.preventDefault();
        if (!isSignedIn) {
            alert('Please sign in to leave a review');
            return;
        }

        if (!newComment.trim()) return;

        setSubmitting(true);
        try {
            const { data, error } = await api.reviews.add(product.id, newRating, newComment);

            if (error) throw error;

            setReviews([data, ...reviews]);
            setNewComment('');
            setNewRating(5);

            // Recalculate average immediately for UI update
            const newTotal = (averageRating * reviews.length) + newRating;
            setAverageRating(Math.round((newTotal / (reviews.length + 1)) * 10) / 10);

        } catch (err) {
            if (await handleAuthError(err)) return;
            alert('Failed to post review: ' + err.message);
        } finally {
            setSubmitting(false);
        }
    };
    
    const handleLoginClick = () => {
        document.body.style.overflow = 'unset';
        onClose();
        window.dispatchEvent(new Event('open-login'));
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content product-preview-modal" onClick={e => e.stopPropagation()}>
                <button className="close-btn" onClick={onClose}><X size={24} /></button>

                <div className="modal-body-grid">
                    {/* Product Image Section */}
                    <div className="modal-image-col">
                        <div className="modal-image-wrapper">
                            {product.imageUrl ? (
                                <img src={product.imageUrl} alt={product.name} />
                            ) : (
                                <div className="placeholder">No Image</div>
                            )}
                        </div>
                    </div>

                    {/* Product Details & Reviews Section */}
                    <div className="modal-details-col">
                        <div className="modal-header-info">
                            <h2 className="modal-title">{product.name}</h2>
                            <div className="modal-meta">
                                <span className="modal-price">{formatPrice(product.price)}</span>
                                {averageRating > 0 && (
                                    <div className="modal-rating-badge">
                                        <Star size={14} fill="#B45309" stroke="#B45309" />
                                        <span>{averageRating} • {reviews.length} reviews</span>
                                    </div>
                                )}
                            </div>

                            <div className={`stock-status ${product.stockQuantity > 0 ? 'stock-in' : 'stock-out'}`}>
                                {product.stockQuantity > 0 ?
                                    <span><CheckCircle size={14} style={{ display: 'inline', marginRight: 4 }} /> In Stock</span> :
                                    'Out of Stock'
                                }
                            </div>

                            <p className="modal-description">
                                {product.description || "Experience the authentic taste of tradition with this premium quality product, sourced directly from the finest ingredients."}
                            </p>

                            <div className="modal-actions">
                                <div className="quantity-selector">
                                    <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>
                                    <span>{quantity}</span>
                                    <button onClick={() => setQuantity(Math.min(product.stockQuantity, quantity + 1))}>+</button>
                                </div>
                                <button
                                    className="add-to-cart-btn"
                                    onClick={() => onAddToCart(quantity)}
                                    disabled={product.stockQuantity === 0}
                                >
                                    {product.stockQuantity === 0 ? 'Out of Stock' : <><ShoppingCart size={20} /> Add to Cart</>}
                                </button>
                            </div>
                        </div>

                        <div className="reviews-section">
                            <h3><Star size={18} fill="#1e293b" stroke="#1e293b" /> Customer Reviews</h3>

                            {/* Review Form */}
                            {isSignedIn ? (
                                <form onSubmit={handleSubmitReview} className="review-form">
                                    <div className="rating-select">
                                        {[1, 2, 3, 4, 5].map(star => (
                                            <button
                                                key={star}
                                                type="button"
                                                onClick={() => setNewRating(star)}
                                                className={star <= newRating ? 'star active' : 'star'}
                                            >
                                                <Star size={20} fill={star <= newRating ? "#EECD5E" : "#cbd5e1"} stroke={star <= newRating ? "#EECD5E" : "#cbd5e1"} />
                                            </button>
                                        ))}
                                    </div>
                                    <div className="input-group">
                                        <input
                                            type="text"
                                            value={newComment}
                                            onChange={e => setNewComment(e.target.value)}
                                            placeholder="Write a review..."
                                            disabled={submitting}
                                        />
                                        <button type="submit" disabled={submitting || !newComment.trim()}>
                                            <Send size={18} />
                                        </button>
                                    </div>
                                </form>
                            ) : (
                                <p className="login-prompt">Please <span onClick={handleLoginClick} style={{ textDecoration: 'underline', cursor: 'pointer', color: 'inherit' }}>sign in</span> to write a review.</p>
                            )}

                            {/* Reviews List */}
                            <div className="reviews-list">
                                {loadingReviews ? (
                                    <p className="loading-text">Loading reviews...</p>
                                ) : reviews.length > 0 ? (
                                    reviews.map(review => (
                                        <div key={review.id} className="review-item">
                                            <div className="review-header">
                                                <span className="reviewer-name">{review.userName || 'Anonymous'}</span>
                                                <div className="review-stars">
                                                    {[...Array(5)].map((_, i) => (
                                                        <Star
                                                            key={i}
                                                            size={14}
                                                            fill={i < review.rating ? "#EECD5E" : "#e2e8f0"}
                                                            stroke={i < review.rating ? "#EECD5E" : "#e2e8f0"}
                                                        />
                                                    ))}
                                                </div>
                                            </div>
                                            <p className="review-text">{review.comment}</p>
                                            <span className="review-date">{formatDate(review.createdAt)}</span>
                                        </div>
                                    ))
                                ) : (
                                    <div className="no-reviews">
                                        <p>No reviews yet</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
