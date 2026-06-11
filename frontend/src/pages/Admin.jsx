import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api, getImageUrl } from '../lib/api';
import { formatPrice } from '../utils/formatters';
import LoadingSpinner from '../components/LoadingSpinner';
import { handleAuthError } from '../utils/auth';
import AdminSidebar from '../components/admin/AdminSidebar';
import AdminOrders from '../components/admin/AdminOrders';
import AdminNotifications from '../components/admin/AdminNotifications';
import './Admin.css';

export default function Admin({ onNavigate }) {
    const { isSignedIn, isLoaded, user } = useAuth();

    // Admin State
    const [activeTab, setActiveTab] = useState('products');
    const [notificationCount, setNotificationCount] = useState(0);

    // Products State
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState('');
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        category: '',
        stockQuantity: '',
    });
    const [error, setError] = useState('');

    useEffect(() => {
        if (isLoaded && !isSignedIn) {
            alert('Please sign in to access admin panel');
            onNavigate('products');
        } else if (isSignedIn) {
            // Very basic check, in real app check roles
            if (activeTab === 'products') {
                loadProducts();
            } else {
                setLoading(false);
            }
            loadNotificationCount();
        }
    }, [isSignedIn, isLoaded, activeTab]);

    const loadNotificationCount = async () => {
        try {
            const { data, error } = await api.admin.getNotifications();
            if (!error && data) {
                const unread = data.filter(n => !n.isRead).length;
                setNotificationCount(unread);
            }
        } catch (error) {
            console.error('Error loading notifications:', error);
        }
    };

    const loadProducts = async () => {
        setLoading(true);
        try {
            const { data, error } = await api.products.getAll();

            if (error) throw error;
            setProducts(data || []);
            setError('');
        } catch (err) {
            console.error('Error loading products:', err);
            if (await handleAuthError(err)) return;
            setError(err.message || 'Failed to load products');
        } finally {
            setLoading(false);
        }
    };

    const openModal = (product) => {
        if (product) {
            setEditingProduct(product);
            setFormData({
                name: product.name,
                description: product.description || '',
                price: product.price.toString(),
                category: product.category || '',
                stockQuantity: product.stockQuantity.toString(),
            });
            setImagePreview(product.imageUrl || '');
        } else {
            setEditingProduct(null);
            setFormData({
                name: '',
                description: '',
                price: '',
                category: '',
                stockQuantity: '0',
            });
            setImagePreview('');
        }
        setImageFile(null);
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingProduct(null);
        setImageFile(null);
        setImagePreview('');
    };

    const handleImageChange = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            setImageFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const productData = {
                name: formData.name,
                description: formData.description || null,
                price: parseFloat(formData.price),
                category: formData.category || null,
                stockQuantity: parseInt(formData.stockQuantity),
            };

            let productId = editingProduct?.id;

            if (editingProduct) {
                // Update existing product
                const { error } = await api.admin.updateProduct(editingProduct.id, productData);
                if (error) throw error;
            } else {
                // Create new product
                const { data, error } = await api.admin.createProduct(productData);
                if (error) throw error;
                productId = data.id;
            }

            // Handle Image Upload if changed
            if (imageFile && productId) {
                 await api.admin.uploadImage(productId, imageFile);
            }

            alert(editingProduct ? 'Product updated successfully!' : 'Product created successfully!');

            closeModal();
            loadProducts();
        } catch (err) {
            if (await handleAuthError(err)) return;
            alert('Error saving product: ' + err.message);
        }
    };

    const deleteProduct = async (product) => {
        if (!confirm(`Are you sure you want to delete "${product.name}"?`)) return;

        try {
            const { error } = await api.admin.deleteProduct(product.id);

            if (error) throw error;
            alert('Product deleted successfully!');
            loadProducts();
        } catch (err) {
            if (await handleAuthError(err)) return;
            alert('Error deleting product: ' + err.message);
        }
    };

    if (!isLoaded) return <LoadingSpinner />;
    if (!isSignedIn) return null;

    return (
        <div className="admin-layout">
            <AdminSidebar
                activeTab={activeTab}
                onTabChange={setActiveTab}
                notificationCount={notificationCount}
            />

            <div className="admin-content">
                <div className="container">
                    {activeTab === 'products' && (
                        <>
                            <div className="page-header">
                                <div>
                                    <h2>Product Management</h2>
                                    <p className="text-muted">Add, edit, and delete products</p>
                                </div>
                                <button className="btn btn-primary" onClick={() => openModal()}>
                                    + Add New Product
                                </button>
                            </div>

                            {error && (
                                <div className="alert alert-danger" style={{ marginBottom: '20px', padding: '10px', background: '#f8d7da', color: '#721c24', borderRadius: '4px' }}>
                                    Error: {error}
                                </div>
                            )}

                            {loading ? (
                                <LoadingSpinner />
                            ) : products.length === 0 && !error ? (
                                <div className="empty-state">
                                    <h3>No products yet</h3>
                                    <p className="text-muted">Create your first product to get started!</p>
                                </div>
                            ) : (
                                <div className="admin-products">
                                    {products.map((product) => (
                                        <div key={product.id} className="admin-product-card card slide-in">
                                            <div className="admin-product-image">
                                                {product.imageUrl ? (
                                                    <img src={getImageUrl(product.imageUrl)} alt={product.name} />
                                                ) : (
                                                    <div className="image-placeholder">No Image</div>
                                                )}
                                            </div>

                                            <div className="admin-product-info">
                                                <h3>{product.name}</h3>
                                                <p className="text-muted text-small">{product.description}</p>
                                                <div className="admin-product-meta">
                                                    <span className="product-price">{formatPrice(product.price)}</span>
                                                    {product.category && (
                                                        <span className="product-category">{product.category}</span>
                                                    )}
                                                    <span className="product-stock">Stock: {product.stockQuantity}</span>
                                                </div>
                                            </div>

                                            <div className="admin-product-actions">
                                                <button
                                                    className="btn btn-secondary btn-sm"
                                                    onClick={() => openModal(product)}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    className="btn btn-danger btn-sm"
                                                    onClick={() => deleteProduct(product)}
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    )}

                    {activeTab === 'orders' && <AdminOrders />}
                    {activeTab === 'notifications' && <AdminNotifications />}
                </div>
            </div>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content card" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>{editingProduct ? 'Edit Product' : 'Add New Product'}</h3>
                            <button className="modal-close" onClick={closeModal}>×</button>
                        </div>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Product Name *</label>
                                <input
                                    type="text"
                                    className="form-input"
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Description</label>
                                <textarea
                                    className="form-textarea"
                                    value={formData.description}
                                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                />
                            </div>

                            <div className="grid grid-2">
                                <div className="form-group">
                                    <label className="form-label">Price *</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        className="form-input"
                                        value={formData.price}
                                        onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Stock Quantity *</label>
                                    <input
                                        type="number"
                                        className="form-input"
                                        value={formData.stockQuantity}
                                        onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Category</label>
                                <select
                                    className="form-input"
                                    value={formData.category}
                                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                                    required
                                >
                                    <option value="">Select Category</option>
                                    <option value="Oils">Oils</option>
                                    <option value="Masalas">Masalas</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Product Image</label>
                                <input
                                    type="file"
                                    className="form-input"
                                    accept="image/*"
                                    onChange={handleImageChange}
                                />
                                {imagePreview && (
                                    <div className="image-preview">
                                        <img src={getImageUrl(imagePreview)} alt="Preview" />
                                    </div>
                                )}
                            </div>

                            <div className="modal-actions">
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingProduct ? 'Update Product' : 'Create Product'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
