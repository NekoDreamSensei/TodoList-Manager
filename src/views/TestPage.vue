<template>
  <div class="test-page">
    <div class="header">
      <h1>�� 测试页面</h1>
      <div class="header-actions">
        <button @click="goBack" class="btn-back">← 返回仪表板</button>
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>

    <!-- 用户信息显示 -->
    <div class="user-info">
      <h3>�� 当前用户信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">用户名:</span>
          <span class="value">{{ currentUser?.username || '未登录' }}</span>
        </div>
        <div class="info-item">
          <span class="label">用户ID:</span>
          <span class="value">{{ currentUser?.id || '无' }}</span>
        </div>
        <div class="info-item">
          <span class="label">认证状态:</span>
          <span class="value" :class="{ 'authenticated': isAuthenticated, 'not-authenticated': !isAuthenticated }">
            {{ isAuthenticated ? '✅ 已认证' : '❌ 未认证' }}
          </span>
        </div>
        <div class="info-item">
          <span class="label">Token状态:</span>
          <span class="value">{{ hasToken ? '✅ 有效' : '❌ 无效' }}</span>
        </div>
      </div>
    </div>

    <!-- API测试区域 -->
    <div class="api-test-section">
      <h3>🔧 API测试区域</h3>
      
      <!-- 测试按钮组 -->
      <div class="test-buttons">
        <button @click="testGetData" :disabled="isLoading" class="btn-test">
          {{ isLoading ? '测试中...' : '测试GET接口' }}
        </button>
        <button @click="testPostData" :disabled="isLoading" class="btn-test">
          {{ isLoading ? '测试中...' : '测试POST接口' }}
        </button>
        <button @click="testAuthCheck" :disabled="isLoading" class="btn-test">
          {{ isLoading ? '测试中...' : '测试认证检查' }}
        </button>
        <button @click="clearResults" class="btn-clear">清除结果</button>
      </div>

      <!-- 测试结果显示 -->
      <div v-if="testResults.length > 0" class="test-results">
        <h4>�� 测试结果</h4>
        <div v-for="(result, index) in testResults" :key="index" class="result-item" :class="result.type">
          <div class="result-header">
            <span class="result-title">{{ result.title }}</span>
            <span class="result-time">{{ result.time }}</span>
          </div>
          <div class="result-content">
            <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>

    <!-- 使用说明 -->
    <div class="usage-guide">
      <h3>�� 使用说明</h3>
      <div class="guide-content">
        <h4>如何新增页面：</h4>
        <ol>
          <li>在 <code>src/views/</code> 目录下创建Vue组件文件</li>
          <li>在 <code>src/router/index.js</code> 中添加路由配置</li>
          <li>设置 <code>meta.requiresAuth</code> 来控制是否需要认证</li>
          <li>使用 <code>authService</code> 进行认证状态管理</li>
        </ol>
        
        <h4>如何调用API：</h4>
        <ol>
          <li>导入 <code>api</code> 服务：<code>import api from '../services/api.js'</code></li>
          <li>使用 <code>api.get()</code>、<code>api.post()</code> 等方法</li>
          <li>API会自动添加JWT token到请求头</li>
          <li>处理响应和错误情况</li>
        </ol>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'
import api from '../services/api.js'

const router = useRouter()
const isLoading = ref(false)
const testResults = ref([])

// 计算属性
const currentUser = computed(() => authService.getCurrentUser())
const isAuthenticated = computed(() => authService.isAuthenticated())
const hasToken = computed(() => !!authService.getToken())

// 页面加载时检查认证状态
onMounted(() => {
  console.log('=== TestPage 页面加载 ===')
  console.log('当前用户:', currentUser.value)
  console.log('认证状态:', isAuthenticated.value)
  console.log('Token状态:', hasToken.value)
})

// 返回仪表板
const goBack = () => {
  router.push('/dashboard')
}

// 退出登录
const logout = async () => {
  try {
    await authService.logout()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    authService.clearAuth()
    router.push('/login')
  }
}

// 添加测试结果
const addTestResult = (title, data, type = 'info') => {
  testResults.value.unshift({
    title,
    data,
    type,
    time: new Date().toLocaleTimeString()
  })
}

// 清除测试结果
const clearResults = () => {
  testResults.value = []
}

// 测试GET接口
const testGetData = async () => {
  isLoading.value = true
  try {
    console.log('开始测试GET接口...')
    const response = await api.get('/test/data')
    console.log('GET接口响应:', response)
    addTestResult('GET /test/data 成功', response, 'success')
  } catch (error) {
    console.error('GET接口测试失败:', error)
    addTestResult('GET /test/data 失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error')
  } finally {
    isLoading.value = false
  }
}

// 测试POST接口
const testPostData = async () => {
  isLoading.value = true
  try {
    console.log('开始测试POST接口...')
    const testData = {
      message: 'Hello from frontend!',
      timestamp: new Date().toISOString(),
      user: currentUser.value?.username || 'anonymous'
    }
    const response = await api.post('/test/data', testData)
    console.log('POST接口响应:', response)
    addTestResult('POST /test/data 成功', response, 'success')
  } catch (error) {
    console.error('POST接口测试失败:', error)
    addTestResult('POST /test/data 失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error')
  } finally {
    isLoading.value = false
  }
}

// 测试认证检查
const testAuthCheck = async () => {
  isLoading.value = true
  try {
    console.log('开始测试认证检查...')
    const response = await authService.checkAuthStatus()
    console.log('认证检查响应:', response)
    addTestResult('认证检查', response, response.success ? 'success' : 'warning')
  } catch (error) {
    console.error('认证检查失败:', error)
    addTestResult('认证检查失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.test-page {
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

.btn-back, .btn-logout {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.btn-back {
  background: #6c757d;
  color: white;
}

.btn-back:hover {
  background: #5a6268;
}

.btn-logout {
  background: #dc3545;
  color: white;
}

.btn-logout:hover {
  background: #c82333;
}

.user-info, .api-test-section, .usage-guide {
  background: white;
  padding: 1.5rem;
  margin-bottom: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h3 {
  color: #333;
  margin: 0 0 1rem 0;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.label {
  font-weight: bold;
  color: #666;
  font-size: 0.9rem;
}

.value {
  color: #333;
  font-size: 1rem;
}

.authenticated {
  color: #28a745;
}

.not-authenticated {
  color: #dc3545;
}

.test-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.btn-test, .btn-clear {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-test {
  background: #007bff;
  color: white;
}

.btn-test:hover:not(:disabled) {
  background: #0056b3;
  transform: translateY(-1px);
}

.btn-test:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
}

.btn-clear {
  background: #6c757d;
  color: white;
}

.btn-clear:hover {
  background: #5a6268;
}

.test-results {
  max-height: 400px;
  overflow-y: auto;
}

.result-item {
  margin-bottom: 1rem;
  padding: 1rem;
  border-radius: 6px;
  border-left: 4px solid #007bff;
}

.result-item.success {
  background: #d4edda;
  border-left-color: #28a745;
}

.result-item.error {
  background: #f8d7da;
  border-left-color: #dc3545;
}

.result-item.warning {
  background: #fff3cd;
  border-left-color: #ffc107;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.result-title {
  font-weight: bold;
  color: #333;
}

.result-time {
  font-size: 0.8rem;
  color: #666;
}

.result-content pre {
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 4px;
  font-size: 0.8rem;
  overflow-x: auto;
  margin: 0;
}

.guide-content h4 {
  color: #333;
  margin: 1rem 0 0.5rem 0;
}

.guide-content ol {
  margin: 0.5rem 0;
  padding-left: 1.5rem;
}

.guide-content li {
  margin: 0.5rem 0;
  line-height: 1.5;
}

.guide-content code {
  background: #f8f9fa;
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}
</style>
