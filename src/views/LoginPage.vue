<template>
  <div class="login-page">
    <div class="auth-container">
      <h1>TodoList 管理系统</h1>
      
      <!-- 检查登录状态按钮 -->
      <div class="auth-check">
        <button @click="checkAuthStatus" class="btn-check" :disabled="isChecking">
          {{ isChecking ? '检查中...' : '检查登录状态' }}
        </button>
        <div v-if="authStatus" class="auth-status">
          <p><strong>认证状态:</strong> {{ authStatus.message }}</p>
          <p v-if="authStatus.user"><strong>用户信息:</strong> {{ authStatus.user.username }}</p>
        </div>
      </div>

      <AuthForm @login="handleLogin" @register="handleRegister" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'
import AuthForm from '../components/AuthForm.vue'

const router = useRouter()
const isChecking = ref(false)
const authStatus = ref(null)

const checkAuthStatus = async () => {
  isChecking.value = true
  authStatus.value = null
  
  try {
    const response = await authService.checkAuthStatus()
    authStatus.value = response
    console.log('认证状态检查成功:', response)
  } catch (error) {
    console.error('认证状态检查失败:', error)
    authStatus.value = {
      message: '检查失败: ' + (error.message || '未知错误'),
      user: null
    }
  } finally {
    isChecking.value = false
  }
}

const handleLogin = (user) => {
  console.log('登录成功:', user)
  router.push('/dashboard')
}

const handleRegister = (user) => {
  console.log('注册成功:', user)
  router.push('/dashboard')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.auth-container {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.auth-check {
  margin-bottom: 2rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.btn-check {
  width: 100%;
  padding: 0.75rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.btn-check:hover:not(:disabled) {
  background: #0056b3;
}

.btn-check:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.auth-status {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  color: #155724;
}

.auth-status p {
  margin: 0.25rem 0;
  font-size: 0.9rem;
}
</style>