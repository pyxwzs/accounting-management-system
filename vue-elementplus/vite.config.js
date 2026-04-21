import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path' 
export default defineConfig({
  plugins: [vue(),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src') 
    }
  },
  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 库中图片为 /image/xxx，开发时转到后端静态映射
      '/image': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
 