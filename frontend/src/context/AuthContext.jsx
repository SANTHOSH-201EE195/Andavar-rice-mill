import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../lib/api';

const AuthContext = createContext({});

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is already logged in
        const token = localStorage.getItem('andavar_token');
        const savedUser = localStorage.getItem('andavar_user');
        
        if (token && savedUser) {
            try {
                setUser(JSON.parse(savedUser));
            } catch (e) {
                console.error("Failed to parse saved user", e);
                signOut();
            }
        }
        setLoading(false);

        // Listen for token expiration events from api.js
        const handleAuthExpired = () => signOut();
        window.addEventListener('auth-expired', handleAuthExpired);
        return () => window.removeEventListener('auth-expired', handleAuthExpired);
    }, []);

    const login = async (mobile, password) => {
        const { data, error } = await api.auth.login(mobile, password);
        if (error) throw error;
        
        // Save token and user info
        localStorage.setItem('andavar_token', data.token);
        
        const userInfo = {
            id: data.id,
            mobile: data.mobile,
            fullName: data.fullName,
            email: data.email,
            role: data.role
        };
        localStorage.setItem('andavar_user', JSON.stringify(userInfo));
        setUser(userInfo);
        
        return data;
    };

    const register = async (mobile, password, fullName, email) => {
        const { data, error } = await api.auth.register(mobile, password, fullName, email);
        if (error) throw error;
        
        // Auto-login after registration
        localStorage.setItem('andavar_token', data.token);
        
        const userInfo = {
            id: data.id,
            mobile: data.mobile,
            fullName: data.fullName,
            email: data.email,
            role: data.role
        };
        localStorage.setItem('andavar_user', JSON.stringify(userInfo));
        setUser(userInfo);
        
        return data;
    };

    const signOut = () => {
        localStorage.removeItem('andavar_token');
        localStorage.removeItem('andavar_user');
        setUser(null);
        // Optionally redirect to home
        window.location.href = '/';
    };

    const value = {
        user,
        loading,
        isLoaded: !loading,
        isSignedIn: !!user,
        login,
        register,
        signOut
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
}

// Custom hook to replace @insforge/react's useUser and useAuth
export function useAuth() {
    return useContext(AuthContext);
}

export function useUser() {
    return useContext(AuthContext);
}
