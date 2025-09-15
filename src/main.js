// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { authService } from './services/authService.js'

// 全局样式
import './assets/main.css'

const app = createApp(App)
const pinia = createPinia()

// 使用Pinia和路由
app.use(pinia)
app.use(router)

// 全局访问认证服务
window.authService = authService

// 应用启动日志
console.log('🎯 TodoList 应用启动')
console.log('📅 启动时间:', new Date().toLocaleString())
console.log('🌍 环境:', import.meta.env.MODE)
console.log('🔗 API地址:', import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080')

// 全局调试函数
window.checkAuth = () => {
  console.log('当前认证状态:', {
    isAuthenticated: authService.isAuthenticated(),
    currentUser: authService.getCurrentUser(),
    token: authService.getToken() ? '存在' : '不存在'
  })
}

app.mount('#app')