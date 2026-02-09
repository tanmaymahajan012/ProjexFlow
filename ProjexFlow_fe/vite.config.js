import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    global: 'globalThis',
  },
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '/ums': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/gms': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/tms': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/pms': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/als': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/mams': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/nms': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})

