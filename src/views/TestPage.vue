<template>
  <div class="test-page">
    <div class="header">
      <h1>🧪 API 测试页面</h1>
      <div class="header-actions">
        <button @click="goBack" class="btn-back">← 返回仪表板</button>
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>

    <!-- 用户信息显示 -->
    <div class="user-info">
      <h3> 当前用户信息</h3>
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
      <h3>🔧 API 连接测试</h3>
      
      <!-- 基础认证测试 -->
      <div class="test-group">
        <h4> 认证相关 API</h4>
        <div class="test-buttons">
          <button @click="testAuthCheck" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '测试认证检查' }}
          </button>
          <button @click="testGetCurrentUser" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '获取当前用户' }}
          </button>
          <button @click="testLogout" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '测试退出登录' }}
          </button>
        </div>
      </div>

      <!-- 专题管理测试 -->
      <div class="test-group">
        <h4> 专题管理 API</h4>
        <div class="test-buttons">
          <button @click="testGetTopics" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '获取专题列表' }}
          </button>
          <button @click="testCreateTopic" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '创建专题' }}
          </button>
          <button @click="testUpdateTopic" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '更新专题' }}
          </button>
          <button @click="testDeleteTopic" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '删除专题' }}
          </button>
        </div>
      </div>

      <!-- 任务管理测试 -->
      <div class="test-group">
        <h4> 任务管理 API</h4>
        <div class="test-buttons">
          <button @click="testGetTasks" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '获取任务列表' }}
          </button>
          <button @click="testCreateTask" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '创建任务' }}
          </button>
          <button @click="testUpdateTask" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '更新任务' }}
          </button>
          <button @click="testDeleteTask" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '删除任务' }}
          </button>
        </div>
      </div>

      <!-- 待办管理测试 -->
      <div class="test-group">
        <h4>✅ 待办管理 API</h4>
        <div class="test-buttons">
          <button @click="testGetTodos" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '获取待办列表' }}
          </button>
          <button @click="testCreateTodo" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '创建待办' }}
          </button>
          <button @click="testUpdateTodo" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '更新待办' }}
          </button>
          <button @click="testDeleteTodo" :disabled="isLoading" class="btn-test">
            {{ isLoading ? '测试中...' : '删除待办' }}
          </button>
        </div>
      </div>

      <!-- 批量测试 -->
      <div class="test-group">
        <h4>🚀 批量测试</h4>
        <div class="test-buttons">
          <button @click="testAllApis" :disabled="isLoading" class="btn-test btn-primary">
            {{ isLoading ? '测试中...' : '测试所有 API' }}
          </button>
          <button @click="clearResults" class="btn-clear">清除结果</button>
        </div>
      </div>

      <!-- 测试结果显示 -->
      <div v-if="testResults.length > 0" class="test-results">
        <h4> 测试结果</h4>
        <div v-for="(result, index) in testResults" :key="index" class="result-item" :class="result.type">
          <div class="result-header">
            <span class="result-title">{{ result.title }}</span>
            <span class="result-time">{{ result.time }}</span>
            <span class="result-status">{{ result.status }}</span>
          </div>
          <div class="result-content">
            <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>

    <!-- 使用说明 -->
    <div class="usage-guide">
      <h3> 使用说明</h3>
      <div class="guide-content">
        <h4>API 测试说明：</h4>
        <ul>
          <li><strong>绿色</strong>：API 调用成功</li>
          <li><strong>红色</strong>：API 调用失败</li>
          <li><strong>黄色</strong>：API 调用警告</li>
          <li><strong>蓝色</strong>：API 调用信息</li>
        </ul>
        
        <h4>测试数据：</h4>
        <p>测试时会创建临时的专题、任务和待办数据，请在生产环境中谨慎使用。</p>
        
        <h4>错误排查：</h4>
        <ol>
          <li>检查后端服务是否启动（端口 8080）</li>
          <li>检查数据库连接是否正常</li>
          <li>检查 JWT token 是否有效</li>
          <li>查看浏览器控制台的详细错误信息</li>
        </ol>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'
import { topicService } from '../services/topicService.js'
import { taskService } from '../services/taskService.js'
import { todoService } from '../services/todoService.js'
import api from '../services/api.js'

const router = useRouter()
const isLoading = ref(false)
const testResults = ref([])
const testData = ref({
  topicId: null,
  taskId: null,
  todoId: null
})

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
const addTestResult = (title, data, type = 'info', status = '') => {
  testResults.value.unshift({
    title,
    data,
    type,
    status,
    time: new Date().toLocaleTimeString()
  })
}

// 清除测试结果
const clearResults = () => {
  testResults.value = []
}

// 测试认证检查
const testAuthCheck = async () => {
  isLoading.value = true
  try {
    const response = await authService.checkAuthStatus()
    addTestResult('认证检查', response, response.success ? 'success' : 'warning', '✅')
  } catch (error) {
    addTestResult('认证检查失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试获取当前用户
const testGetCurrentUser = async () => {
  isLoading.value = true
  try {
    const response = await authService.getCurrentUser()
    addTestResult('获取当前用户', response, 'success', '✅')
  } catch (error) {
    addTestResult('获取当前用户失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试退出登录
const testLogout = async () => {
  isLoading.value = true
  try {
    const response = await authService.logout()
    addTestResult('退出登录', response, 'success', '✅')
  } catch (error) {
    addTestResult('退出登录失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试获取专题列表
const testGetTopics = async () => {
  if (!currentUser.value?.id) {
    addTestResult('获取专题列表', { error: '用户未登录' }, 'error', '❌')
    return
  }
  
  isLoading.value = true
  try {
    const response = await topicService.getTopicsByUserId(currentUser.value.id)
    addTestResult('获取专题列表', response, 'success', '✅')
  } catch (error) {
    addTestResult('获取专题列表失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试创建专题
const testCreateTopic = async () => {
  if (!currentUser.value?.id) {
    addTestResult('创建专题', { error: '用户未登录' }, 'error', '❌')
    return
  }
  
  isLoading.value = true
  try {
    const topicData = {
      name: `测试专题_${Date.now()}`,
      description: '这是一个测试专题'
    }
    const response = await topicService.createTopic(topicData, currentUser.value.id)
    testData.value.topicId = response.id
    addTestResult('创建专题', response, 'success', '✅')
  } catch (error) {
    addTestResult('创建专题失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试更新专题
const testUpdateTopic = async () => {
  if (!testData.value.topicId) {
    addTestResult('更新专题', { error: '请先创建专题' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const topicData = {
      id: testData.value.topicId,
      name: `更新后的专题_${Date.now()}`,
      description: '这是更新后的测试专题'
    }
    const response = await topicService.updateTopic(testData.value.topicId, topicData)
    addTestResult('更新专题', response, 'success', '✅')
  } catch (error) {
    addTestResult('更新专题失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试删除专题
const testDeleteTopic = async () => {
  if (!testData.value.topicId) {
    addTestResult('删除专题', { error: '请先创建专题' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const response = await topicService.deleteTopic(testData.value.topicId)
    testData.value.topicId = null
    addTestResult('删除专题', response, 'success', '✅')
  } catch (error) {
    addTestResult('删除专题失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试获取任务列表
const testGetTasks = async () => {
  if (!testData.value.topicId) {
    addTestResult('获取任务列表', { error: '请先创建专题' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const response = await taskService.getTasksByTopicId(testData.value.topicId)
    addTestResult('获取任务列表', response, 'success', '✅')
  } catch (error) {
    addTestResult('获取任务列表失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试创建任务
const testCreateTask = async () => {
  if (!testData.value.topicId) {
    addTestResult('创建任务', { error: '请先创建专题' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const taskData = {
      title: `测试任务_${Date.now()}`,
      description: '这是一个测试任务',
      status: '待开始',
      priority: '中'
    }
    const response = await taskService.createTask(taskData, testData.value.topicId)
    testData.value.taskId = response.id
    addTestResult('创建任务', response, 'success', '✅')
  } catch (error) {
    addTestResult('创建任务失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试更新任务
const testUpdateTask = async () => {
  if (!testData.value.taskId) {
    addTestResult('更新任务', { error: '请先创建任务' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const taskData = {
      id: testData.value.taskId,
      title: `更新后的任务_${Date.now()}`,
      description: '这是更新后的测试任务',
      status: '进行中',
      priority: '高'
    }
    const response = await taskService.updateTask(testData.value.taskId, taskData)
    addTestResult('更新任务', response, 'success', '✅')
  } catch (error) {
    addTestResult('更新任务失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试删除任务
const testDeleteTask = async () => {
  if (!testData.value.taskId) {
    addTestResult('删除任务', { error: '请先创建任务' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const response = await taskService.deleteTask(testData.value.taskId)
    testData.value.taskId = null
    addTestResult('删除任务', response, 'success', '✅')
  } catch (error) {
    addTestResult('删除任务失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试获取待办列表
const testGetTodos = async () => {
  if (!testData.value.taskId) {
    addTestResult('获取待办列表', { error: '请先创建任务' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const response = await todoService.getTodosByTaskId(testData.value.taskId)
    addTestResult('获取待办列表', response, 'success', '✅')
  } catch (error) {
    addTestResult('获取待办列表失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试创建待办
const testCreateTodo = async () => {
  if (!testData.value.taskId) {
    addTestResult('创建待办', { error: '请先创建任务' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const todoData = {
      content: `测试待办_${Date.now()}`,
      isCompleted: false
    }
    const response = await todoService.createTodo(todoData, testData.value.taskId)
    testData.value.todoId = response.id
    addTestResult('创建待办', response, 'success', '✅')
  } catch (error) {
    addTestResult('创建待办失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试更新待办
const testUpdateTodo = async () => {
  if (!testData.value.todoId) {
    addTestResult('更新待办', { error: '请先创建待办' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const todoData = {
      id: testData.value.todoId,
      content: `更新后的待办_${Date.now()}`,
      isCompleted: true
    }
    const response = await todoService.updateTodo(testData.value.todoId, todoData)
    addTestResult('更新待办', response, 'success', '✅')
  } catch (error) {
    addTestResult('更新待办失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试删除待办
const testDeleteTodo = async () => {
  if (!testData.value.todoId) {
    addTestResult('删除待办', { error: '请先创建待办' }, 'warning', '⚠️')
    return
  }
  
  isLoading.value = true
  try {
    const response = await todoService.deleteTodo(testData.value.todoId)
    testData.value.todoId = null
    addTestResult('删除待办', response, 'success', '✅')
  } catch (error) {
    addTestResult('删除待办失败', {
      error: error.message,
      status: error.response?.status,
      data: error.response?.data
    }, 'error', '❌')
  } finally {
    isLoading.value = false
  }
}

// 测试所有 API
const testAllApis = async () => {
  if (!currentUser.value?.id) {
    addTestResult('批量测试', { error: '用户未登录' }, 'error', '❌')
    return
  }
  
  isLoading.value = true
  const tests = [
    () => testAuthCheck(),
    () => testGetTopics(),
    () => testCreateTopic(),
    () => testCreateTask(),
    () => testCreateTodo(),
    () => testUpdateTodo(),
    () => testUpdateTask(),
    () => testUpdateTopic(),
    () => testDeleteTodo(),
    () => testDeleteTask(),
    () => testDeleteTopic()
  ]
  
  for (const test of tests) {
    try {
      await test()
      await new Promise(resolve => setTimeout(resolve, 500)) // 延迟500ms
    } catch (error) {
      console.error('批量测试中的错误:', error)
    }
  }
  
  isLoading.value = false
  addTestResult('批量测试完成', { message: '所有测试已执行完成' }, 'info', 'ℹ️')
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

.test-group {
  margin-bottom: 2rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
}

.test-group h4 {
  color: #333;
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
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

.btn-primary {
  background: #28a745;
}

.btn-primary:hover:not(:disabled) {
  background: #218838;
}

.btn-clear {
  background: #6c757d;
  color: white;
}

.btn-clear:hover {
  background: #5a6268;
}

.test-results {
  max-height: 500px;
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

.result-item.info {
  background: #d1ecf1;
  border-left-color: #17a2b8;
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

.result-status {
  font-size: 1.2rem;
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

.guide-content ul, .guide-content ol {
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
