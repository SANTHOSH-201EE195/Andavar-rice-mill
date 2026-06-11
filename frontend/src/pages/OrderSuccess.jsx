import { CheckCircle } from 'lucide-react';
import './OrderSuccess.css';

export default function OrderSuccess({ onNavigate }) {
    return (
        <div className="order-success-page">
            <div className="container">
                <div className="success-card">
                    <div className="success-icon-wrapper">
                        <CheckCircle size={40} strokeWidth={3} />
                    </div>

                    <h1 className="success-title">Order Successful!</h1>
                    <p className="success-message">
                        Thank you for your purchase. Your order has been placed successfully and is being processed.
                        You can track your order status in the My Orders section.
                    </p>

                    <div className="success-actions">
                        <button
                            className="btn-success-primary"
                            onClick={() => onNavigate('my-orders')}
                        >
                            View My Orders
                        </button>
                        <button
                            className="btn-success-outline"
                            onClick={() => onNavigate('products')}
                        >
                            Continue Shopping
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
