import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/product-images': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/images/oil/': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/images/masala/': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
