<template>
  <div class="dashboard-page">
    <div class="header">
      <h1>仪表板</h1>
      <div class="header-actions">
        <!-- 检查登录状态按钮 -->
        <button @click="checkAuthStatus" class="btn-check" :disabled="isChecking">
          {{ isChecking ? '检查中...' : '检查登录状态' }}
        </button>
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>

    <!-- 认证状态显示 -->
    <div v-if="authStatus" class="auth-status">
      <h3>认证状态信息</h3>
      <p><strong>状态:</strong> {{ authStatus.message }}</p>
      <p v-if="authStatus.user"><strong>用户:</strong> {{ authStatus.user.username }}</p>
      <p v-if="authStatus.user"><strong>用户ID:</strong> {{ authStatus.user.id }}</p>
    </div>

    <div class="content">
      <p>欢迎来到仪表板！</p>
      <p>当前用户: {{ currentUser?.username || '未登录' }}</p>
      <p>认证状态: {{ isAuthenticated ? '已认证' : '未认证' }}</p>
      
      <!-- 添加测试页面链接 -->
      <div class="quick-actions">
        <h3>快速操作</h3>
        <button @click="goToTestPage" class="btn-test-page">
          🧪 进入测试页面
        </button>
      </div>
      
      <p>调试信息: {{ JSON.stringify({ hasUser: !!currentUser, hasToken: !!authService.getToken() }, null, 2) }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'

const router = useRouter()
const isChecking = ref(false)
const authStatus = ref(null)

const currentUser = computed(() => {
  const user = authService.getCurrentUser()
  console.log('DashboardPage currentUser computed:', user)
  return user
})

const isAuthenticated = computed(() => {
  const authenticated = authService.isAuthenticated()
  console.log('DashboardPage isAuthenticated computed:', authenticated)
  return authenticated
})

// 页面加载时检查认证状态
onMounted(async () => {
  console.log('=== DashboardPage 页面加载 ===')
  console.log('当前用户:', authService.getCurrentUser())
  console.log('是否认证:', authService.isAuthenticated())
  console.log('sessionStorage检查:', {
    currentUser: sessionStorage.getItem('currentUser'),
    authToken: sessionStorage.getItem('authToken')
  })
  
  // 延迟检查，确保authService完全初始化
  setTimeout(async () => {
    console.log('延迟检查认证状态...')
    console.log('延迟后当前用户:', authService.getCurrentUser())
    console.log('延迟后是否认证:', authService.isAuthenticated())
    
    // 如果本地没有认证信息，尝试从后端检查
    if (!authService.isAuthenticated()) {
      console.log('本地没有认证信息，尝试从后端检查...')
      try {
        await checkAuthStatus()
      } catch (error) {
        console.error('页面加载时检查认证状态失败:', error)
      }
    }
  }, 100)
})

const checkAuthStatus = async () => {
  isChecking.value = true
  authStatus.value = null
  
  try {
    console.log('=== DashboardPage 开始检查认证状态 ===')
    const response = await authService.checkAuthStatus()
    console.log('=== DashboardPage 收到响应 ===')
    console.log('响应:', response)
    console.log('响应类型:', typeof response)
    console.log('响应success:', response.success)
    console.log('响应user:', response.user)
    
    authStatus.value = response
    console.log('认证状态检查成功:', response)
  } catch (error) {
    console.error('认证状态检查失败:', error)
    authStatus.value = {
      success: false,
      message: '检查失败: ' + (error.message || '未知错误'),
      user: null
    }
  } finally {
    isChecking.value = false
  }
}

const logout = async () => {
  try {
    console.log('开始退出登录...')
    await authService.logout()
    console.log('退出登录成功')
    router.push('/')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使后端调用失败，也要清除本地状态
    authService.clearAuth()
    router.push('/')
  }
}

const goToTestPage = () => {
  router.push('/test')
}
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h1 {
  color: #333;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.btn-check, .btn-logout {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.btn-check {
  background: #007bff;
  color: white;
}

.btn-check:hover:not(:disabled) {
  background: #0056b3;
}

.btn-check:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.btn-logout {
  background: #dc3545;
  color: white;
}

.btn-logout:hover {
  background: #c82333;
}

.auth-status {
  margin-bottom: 2rem;
  padding: 1rem;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 8px;
  color: #155724;
}

.auth-status h3 {
  margin: 0 0 1rem 0;
  color: #155724;
}

.auth-status p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content p {
  margin: 1rem 0;
  color: #666;
  font-size: 1.1rem;
}

.quick-actions {
  margin: 2rem 0;
  padding: 1rem;
  background: #e9ecef;
  border-radius: 8px;
}

.quick-actions h3 {
  margin: 0 0 1rem 0;
  color: #333;
}

.btn-test-page {
  padding: 0.75rem 1.5rem;
  background: #17a2b8;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.btn-test-page:hover {
  background: #138496;
}
</style>