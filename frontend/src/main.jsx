import { Component } from 'react';
import { createRoot } from 'react-dom/client';
import { AuthProvider } from './context/AuthContext';
import './index.css';
import App from './App';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Uncaught error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '20px', textAlign: 'center' }}>
          <h1>Something went wrong.</h1>
          <p>{this.state.error && this.state.error.toString()}</p>
        </div>
      );
    }
    return this.props.children;
  }
}

createRoot(rootElement).render(
  <AuthProvider>
    <ErrorBoundary>
      <App />
    </ErrorBoundary>
  </AuthProvider>
);
