import { useEffect, useState } from 'react';
import { ShoppingBag, Menu, X, User as UserIcon, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';
import LoginModal from './LoginModal';
import './Header.css';

export default function Header({ currentPage, onNavigate }) {
    const [cartCount, setCartCount] = useState(0);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const { user, isSignedIn, signOut } = useAuth();

    useEffect(() => {
        loadCartCount();

        const handleCartUpdate = () => loadCartCount();
        const handleOpenLogin = () => setIsLoginModalOpen(true);

        window.addEventListener('cart-updated', handleCartUpdate);
        window.addEventListener('open-login', handleOpenLogin);

        return () => {
            window.removeEventListener('cart-updated', handleCartUpdate);
            window.removeEventListener('open-login', handleOpenLogin);
        };
    }, [user]);

    const loadCartCount = async () => {
        try {
            if (!user) {
                setCartCount(0);
                return;
            }

            const { data, error } = await api.cart.get();
            if (error) throw error;

            setCartCount(data?.length || 0);
        } catch (error) {
            console.error('Error loading cart count:', error);
        }
    };

    const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
    const closeMenu = () => setIsMenuOpen(false);

    const scrollToSection = (sectionId) => {
        if (currentPage !== 'products') {
            onNavigate('products');
            // Give time for page to mount
            setTimeout(() => {
                const element = document.getElementById(sectionId);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth' });
                } else {
                    window.scrollTo(0, 0);
                }
            }, 100);
        } else {
            const element = document.getElementById(sectionId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth' });
            }
        }
    };

    const handleNav = (target) => {
        if (target === 'admin') {
            onNavigate('admin');
        } else if (target === 'cart') {
            onNavigate('cart');
        } else if (target === 'my-orders') {
            onNavigate('my-orders');
        } else if (target === 'home') {
            onNavigate('products');
            window.scrollTo(0, 0);
        } else {
            // Scroll targets
            scrollToSection(target);
        }
        closeMenu();
    };

    const handleLogout = () => {
        setIsDropdownOpen(false);
        signOut();
    };

    return (
        <>
            <header className="header">
                {/* Mobile Overlay */}
                <div className={`overlay ${isMenuOpen ? 'open' : ''}`} onClick={closeMenu}></div>

                <div className="container">
                    <div className="header-content">
                        {/* Mobile Toggle */}
                        <button className="mobile-menu-btn" onClick={toggleMenu}>
                            {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
                        </button>

                        <div className="header-brand" onClick={() => handleNav('home')}>
                            <img src="/images/andavar.png" alt="Andavar" className="brand-logo-img" />
                            <span className="brand-name">Andavar</span>
                        </div>

                        <nav className={`header-nav ${isMenuOpen ? 'open' : ''}`}>
                            <button
                                className={`nav-link ${currentPage === 'products' ? 'active' : ''}`}
                                onClick={() => handleNav('home')}
                            >
                                Home
                            </button>
                            <button className="nav-link" onClick={() => handleNav('services')}>Services</button>
                            <button
                                className={`nav-link ${currentPage === 'shop' ? 'active' : ''}`}
                                onClick={() => handleNav('shop')}
                            >
                                Shop
                            </button>
                            {isSignedIn && (
                                <>
                                    <button
                                        className={`nav-link ${currentPage === 'my-orders' ? 'active' : ''}`}
                                        onClick={() => handleNav('my-orders')}
                                    >
                                        My Orders
                                    </button>
                                    {user?.role === 'ROLE_ADMIN' && (
                                        <button
                                            className={`nav-link ${currentPage === 'admin' ? 'active' : ''}`}
                                            onClick={() => handleNav('admin')}
                                        >
                                            Admin
                                        </button>
                                    )}
                                </>
                            )}
                        </nav>

                        <div className="header-actions">
                            <button
                                className="icon-btn cart-btn"
                                onClick={() => handleNav('cart')}
                            >
                                <ShoppingBag size={20} />
                                {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                            </button>

                            <div className="auth-wrapper">
                                {!isSignedIn ? (
                                    <div className="signin-btn-wrapper">
                                        <button 
                                            className="btn btn-primary btn-sm"
                                            onClick={() => setIsLoginModalOpen(true)}
                                        >
                                            Sign In
                                        </button>
                                    </div>
                                ) : (
                                    <div className="user-dropdown-container">
                                        <button 
                                            className="user-avatar-btn"
                                            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                        >
                                            <div className="avatar-circle">
                                                {user?.fullName?.charAt(0).toUpperCase() || 'U'}
                                            </div>
                                        </button>
                                        
                                        {isDropdownOpen && (
                                            <div className="user-dropdown-menu card">
                                                <div className="dropdown-header">
                                                    <p className="user-name">{user?.fullName}</p>
                                                    <p className="user-mobile text-muted">{user?.mobile}</p>
                                                </div>
                                                <div className="dropdown-divider"></div>
                                                <button 
                                                    className="dropdown-item"
                                                    onClick={() => {
                                                        handleNav('my-orders');
                                                        setIsDropdownOpen(false);
                                                    }}
                                                >
                                                    <ShoppingBag size={16} /> My Orders
                                                </button>
                                                {user?.role === 'ROLE_ADMIN' && (
                                                    <button 
                                                        className="dropdown-item"
                                                        onClick={() => {
                                                            handleNav('admin');
                                                            setIsDropdownOpen(false);
                                                        }}
                                                    >
                                                        <UserIcon size={16} /> Admin Panel
                                                    </button>
                                                )}
                                                <div className="dropdown-divider"></div>
                                                <button 
                                                    className="dropdown-item text-danger"
                                                    onClick={handleLogout}
                                                >
                                                    <LogOut size={16} /> Sign Out
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <LoginModal 
                isOpen={isLoginModalOpen} 
                onClose={() => setIsLoginModalOpen(false)} 
            />
        </>
    );
}

