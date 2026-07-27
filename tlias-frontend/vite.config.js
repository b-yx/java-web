import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,                //固定端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,     // 必须，解决跨域
        secure: false,          // 兼容自签名证书
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)) // 绝对路径更稳健
    }
  }
})