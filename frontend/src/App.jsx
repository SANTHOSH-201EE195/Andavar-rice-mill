import { useState } from 'react';
import { useAuth } from './context/AuthContext';
import Header from './components/Header';
import Footer from './components/Footer';
import ProductList from './pages/ProductList';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import Admin from './pages/Admin';
import OrderSuccess from './pages/OrderSuccess';
import MyOrders from './pages/MyOrders';
import './App.css';

// Main Application Component
function App() {
  const [currentPage, setCurrentPage] = useState('products');
  const { user } = useAuth();

  const handleNavigate = (page) => {
    setCurrentPage(page);
    window.scrollTo(0, 0);
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'products':
        return <ProductList onNavigate={handleNavigate} />;
      case 'cart':
        return <Cart onNavigate={handleNavigate} />;
      case 'checkout':
        return <Checkout onNavigate={handleNavigate} />;
      case 'admin':
        // Protect Admin Route
        if (user?.role !== 'ROLE_ADMIN') {
          return <ProductList onNavigate={handleNavigate} />;
        }
        return <Admin onNavigate={handleNavigate} />;
      case 'order-success':
        return <OrderSuccess onNavigate={handleNavigate} />;
      case 'my-orders':
        return <MyOrders onNavigate={handleNavigate} />;
      default:
        return <ProductList onNavigate={handleNavigate} />;
    }
  };

  return (
    <div className="app">
      <Header currentPage={currentPage} onNavigate={handleNavigate} />
      <main>{renderPage()}</main>
      <Footer />
    </div>
  );
}

export default App;
