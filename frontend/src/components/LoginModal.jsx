import { useState } from 'react';
import { X, Eye, EyeOff, Lock, Phone, User, Mail, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import './LoginModal.css';

export default function LoginModal({ isOpen, onClose }) {
    const { login, register } = useAuth();
    
    // View states: 'login', 'register', 'forgot'
    const [view, setView] = useState('login');
    const [showPassword, setShowPassword] = useState(false);
    
    // Form state
    const [formData, setFormData] = useState({
        mobile: '',
        password: '',
        fullName: '',
        email: ''
    });
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    if (!isOpen) return null;

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError(''); // clear error when typing
    };

    const validateMobile = (mobile) => {
        const regex = /^[0-9]{10}$/;
        return regex.test(mobile);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMsg('');
        
        // Basic validation
        if (!validateMobile(formData.mobile)) {
            setError('Please enter a valid 10-digit mobile number.');
            return;
        }

        setLoading(true);

        try {
            if (view === 'login') {
                await login(formData.mobile, formData.password);
                onClose(); // Close modal on success
            } else if (view === 'register') {
                if (formData.password.length < 6) {
                    throw new Error('Password must be at least 6 characters.');
                }
                if (!formData.fullName.trim()) {
                    throw new Error('Full name is required.');
                }
                
                await register(formData.mobile, formData.password, formData.fullName, formData.email);
                onClose(); // Close modal on success
            } else if (view === 'forgot') {
                // In a real app, this would trigger an OTP to the mobile
                // For now, we simulate success
                setTimeout(() => {
                    setSuccessMsg('A password reset link/OTP has been sent to your mobile number.');
                    setLoading(false);
                }, 1500);
                return; // Early return so loading doesn't get set to false again below
            }
        } catch (err) {
            setError(err.message || 'An error occurred. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const resetForm = () => {
        setFormData({ mobile: '', password: '', fullName: '', email: '' });
        setError('');
        setSuccessMsg('');
    };

    const switchView = (newView) => {
        setView(newView);
        resetForm();
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content login-modal" onClick={e => e.stopPropagation()}>
                <button className="close-btn" onClick={onClose}>
                    <X size={24} />
                </button>

                <div className="modal-header">
                    <h2>
                        {view === 'login' ? 'Welcome Back' : 
                         view === 'register' ? 'Create Account' : 
                         'Reset Password'}
                    </h2>
                    <p className="text-muted">
                        {view === 'login' ? 'Sign in to access your orders and cart.' : 
                         view === 'register' ? 'Join Andavar for authentic natural products.' : 
                         'Enter your mobile number to reset your password.'}
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    {error && (
                        <div className="error-alert">
                            <AlertCircle size={18} />
                            <span>{error}</span>
                        </div>
                    )}
                    
                    {successMsg && (
                        <div className="success-alert">
                            <span>{successMsg}</span>
                        </div>
                    )}

                    {view === 'register' && (
                        <>
                            <div className="form-group input-with-icon">
                                <User size={20} className="input-icon" />
                                <input
                                    type="text"
                                    name="fullName"
                                    placeholder="Full Name *"
                                    className="form-input"
                                    value={formData.fullName}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="form-group input-with-icon">
                                <Mail size={20} className="input-icon" />
                                <input
                                    type="email"
                                    name="email"
                                    placeholder="Email Address (Optional)"
                                    className="form-input"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </>
                    )}

                    <div className="form-group input-with-icon">
                        <Phone size={20} className="input-icon" />
                        <input
                            type="tel"
                            name="mobile"
                            placeholder="Mobile Number (e.g. 9876543210) *"
                            className="form-input"
                            value={formData.mobile}
                            onChange={handleInputChange}
                            required
                            maxLength="10"
                            pattern="[0-9]{10}"
                        />
                    </div>

                    {view !== 'forgot' && (
                        <div className="form-group input-with-icon password-group">
                            <Lock size={20} className="input-icon" />
                            <input
                                type={showPassword ? "text" : "password"}
                                name="password"
                                placeholder="Password *"
                                className="form-input"
                                value={formData.password}
                                onChange={handleInputChange}
                                required
                                minLength="6"
                            />
                            <button 
                                type="button" 
                                className="toggle-password"
                                onClick={() => setShowPassword(!showPassword)}
                                tabIndex="-1"
                            >
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>
                    )}

                    {view === 'login' && (
                        <div className="forgot-password-link">
                            <button type="button" onClick={() => switchView('forgot')}>
                                Forgot Password?
                            </button>
                        </div>
                    )}

                    <button 
                        type="submit" 
                        className="btn btn-primary submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Processing...' : 
                         view === 'login' ? 'Sign In' : 
                         view === 'register' ? 'Register' : 
                         'Send Reset Link'}
                    </button>
                </form>

                <div className="modal-footer">
                    {view === 'login' ? (
                        <p>Don't have an account? <button onClick={() => switchView('register')} className="text-link">Sign up</button></p>
                    ) : view === 'register' ? (
                        <p>Already have an account? <button onClick={() => switchView('login')} className="text-link">Sign in</button></p>
                    ) : (
                        <p>Remember your password? <button onClick={() => switchView('login')} className="text-link">Sign in</button></p>
                    )}
                </div>
            </div>
        </div>
    );
}
