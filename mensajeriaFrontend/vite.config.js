import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 80,
    proxy: {
      '/courier': {
        target: 'http://courier-app:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/courier/, '')
      },
      '/tenant': {
        target: 'http://tenant-app:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/tenant/, '')
      }
    }
  }
})
