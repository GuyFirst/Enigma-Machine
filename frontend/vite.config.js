import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// In dev, /api is proxied to the local Spring Boot server - no CORS needed.
// In prod, the frontend calls the deployed backend via VITE_API_URL instead.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
