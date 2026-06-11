// src/lib/api.js

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Gets the JWT token from localStorage
 */
const getToken = () => localStorage.getItem('andavar_token');

/**
 * Helper to build headers with Authorization if token exists
 */
const getHeaders = () => {
    const headers = {
        'Content-Type': 'application/json',
    };
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
};

/**
 * Generic API request function
 */
async function request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                ...getHeaders(),
                ...options.headers,
            },
        });

        // Parse JSON if possible
        const isJson = response.headers.get('content-type')?.includes('application/json');
        const data = isJson ? await response.json() : null;

        if (!response.ok) {
            // Handle 401 Unauthorized (token expired)
            if (response.status === 401) {
                localStorage.removeItem('andavar_token');
                window.dispatchEvent(new Event('auth-expired'));
                throw new Error('Session expired. Please log in again.');
            }
            
            const error = (data && data.message) || response.statusText;
            throw new Error(error);
        }

        return { data, error: null };
    } catch (error) {
        console.error(`API Error (${endpoint}):`, error);
        return { data: null, error };
    }
}

// ==========================================
// API CLIENT
// ==========================================

export const api = {
    // --- Auth ---
    auth: {
        login: (mobile, password) => 
            request('/auth/login', { method: 'POST', body: JSON.stringify({ mobile, password }) }),
            
        register: (mobile, password, fullName, email) => 
            request('/auth/register', { method: 'POST', body: JSON.stringify({ mobile, password, fullName, email }) }),
    },

    // --- Products ---
    products: {
        getAll: (category = null) => 
            request(`/products${category ? `?category=${category}` : ''}`),
            
        getById: (id) => request(`/products/${id}`),
    },

    // --- Cart ---
    cart: {
        get: () => request('/cart'),
        
        add: (productId, quantity = 1) => 
            request('/cart', { method: 'POST', body: JSON.stringify({ productId, quantity }) }),
            
        update: (cartItemId, quantity) => 
            request(`/cart/${cartItemId}`, { method: 'PUT', body: JSON.stringify({ quantity }) }),
            
        remove: (cartItemId) => 
            request(`/cart/${cartItemId}`, { method: 'DELETE' }),
    },

    // --- Orders ---
    orders: {
        getMyOrders: () => request('/orders'),
        
        placeOrder: (shippingDetails) => 
            request('/orders', { method: 'POST', body: JSON.stringify(shippingDetails) }),
    },

    // --- Reviews ---
    reviews: {
        getByProduct: (productId) => request(`/reviews/${productId}`),
        
        getAllRatings: () => request('/ratings'),
        
        add: (productId, rating, comment) => 
            request('/reviews', { method: 'POST', body: JSON.stringify({ productId, rating, comment }) }),
    },

    // --- Admin ---
    admin: {
        // Products
        createProduct: (productData) => 
            request('/admin/products', { method: 'POST', body: JSON.stringify(productData) }),
            
        updateProduct: (id, productData) => 
            request(`/admin/products/${id}`, { method: 'PUT', body: JSON.stringify(productData) }),
            
        deleteProduct: (id) => 
            request(`/admin/products/${id}`, { method: 'DELETE' }),
            
        uploadImage: async (productId, file) => {
            const formData = new FormData();
            formData.append('file', file);
            
            const token = getToken();
            const response = await fetch(`${API_BASE_URL}/admin/products/${productId}/image`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }, // No Content-Type here, browser sets boundary
                body: formData
            });
            
            if (!response.ok) throw new Error('Image upload failed');
            return response.json();
        },

        // Orders
        getAllOrders: () => request('/admin/orders'),
        updateOrderStatus: (id, status) => 
            request(`/admin/orders/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),

        // Notifications
        getNotifications: () => request('/admin/notifications'),
        markNotificationRead: (id) => 
            request(`/admin/notifications/${id}/read`, { method: 'PUT' }),
        deleteNotification: (id) => 
            request(`/admin/notifications/${id}`, { method: 'DELETE' }),
    }
};
