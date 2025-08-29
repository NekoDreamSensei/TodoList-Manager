<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import AuthForm from './components/AuthForm.vue'
import CreateTopicModal from './components/CreateTopicModal.vue'
import CreateTaskModal from './components/CreateTaskModal.vue'
import CreateTodoModal from './components/CreateTodoModal.vue'
import EditTodoModal from './components/EditTodoModal.vue'
import { parseMarkdownToJson, convertJsonToMarkdown, validateMarkdownFormat } from './utils/markdownParser.js'

// 响应式数据
const currentUser = ref(null)
const topics = ref([])
const selectedTopic = ref(null)
const selectedTask = ref(null)
const showCreateTopic = ref(false)
const showCreateTask = ref(false)
const showCreateTodo = ref(false)
const editingTodo = ref(null)

// 初始化数据
onMounted(() => {
  loadData()
})

// 加载数据
const loadData = () => {
  const savedUser = localStorage.getItem('currentUser')
  
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
    // 加载当前用户的专题数据
    loadUserTopics()
  }
}

// 加载用户专题数据
const loadUserTopics = () => {
  if (!currentUser.value) return
  
  const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
  const userTopics = allTopics[currentUser.value.username] || []
  topics.value = userTopics
}

// 保存数据
const saveData = () => {
  if (!currentUser.value) return
  
  const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
  allTopics[currentUser.value.username] = topics.value
  localStorage.setItem('topics', JSON.stringify(allTopics))
}

// 用户认证
const handleLogin = (user) => {
  currentUser.value = user
  localStorage.setItem('currentUser', JSON.stringify(user))
  // 登录后加载用户数据
  loadUserTopics()
}

const handleRegister = (user) => {
  currentUser.value = user
  localStorage.setItem('currentUser', JSON.stringify(user))
  // 注册后初始化用户数据
  topics.value = []
  saveData()
}

const logout = () => {
  currentUser.value = null
  selectedTopic.value = null
  selectedTask.value = null
  topics.value = []
  localStorage.removeItem('currentUser')
}

// 专题管理
const createTopic = (topicData) => {
  const newTopic = {
    id: Date.now().toString(),
    name: topicData.name,
    description: topicData.description,
    createdAt: new Date().toISOString(),
    tasks: []
  }
  
  topics.value.push(newTopic)
  saveData()
  showCreateTopic.value = false
}

const selectTopic = (topic) => {
  selectedTopic.value = topic
  selectedTask.value = null
}

// 任务管理
const createTask = (taskData) => {
  const newTask = {
    id: Date.now().toString(),
    name: taskData.name,
    description: taskData.description,
    createdAt: new Date().toISOString(),
    todos: []
  }
  
  selectedTopic.value.tasks.push(newTask)
  saveData()
  showCreateTask.value = false
}

const selectTask = (task) => {
  selectedTask.value = task
}

// 待办管理
const createTodo = (todoData) => {
  const newTodo = {
    id: Date.now().toString(),
    title: todoData.title,
    description: todoData.description,
    completed: false,
    progress: 0,
    note: '',
    createdAt: new Date().toISOString()
  }
  
  selectedTask.value.todos.push(newTodo)
  saveData()
  showCreateTodo.value = false
}

const toggleTodo = (todo) => {
  todo.completed = !todo.completed
  if (todo.completed) {
    todo.progress = 100
  }
  saveData()
}

const updateTodoProgress = (todo) => {
  if (todo.progress === 100) {
    todo.completed = true
  } else {
    todo.completed = false
  }
  saveData()
}

const editTodo = (todo) => {
  editingTodo.value = { ...todo }
}

const updateTodo = (updatedTodo) => {
  const index = selectedTask.value.todos.findIndex(t => t.id === updatedTodo.id)
  if (index !== -1) {
    selectedTask.value.todos[index] = { ...updatedTodo }
    saveData()
  }
  editingTodo.value = null
}

// 进度计算
const getTopicProgress = (topic) => {
  if (!topic.tasks || topic.tasks.length === 0) return 0
  
  const totalProgress = topic.tasks.reduce((sum, task) => {
    return sum + getTaskProgress(task)
  }, 0)
  
  return Math.round(totalProgress / topic.tasks.length)
}

const getTaskProgress = (task) => {
  if (!task.todos || task.todos.length === 0) return 0
  
  const totalCount = task.todos.length
  let totalProgress = 0
  
  // 遍历每个待办，计算实际进度
  task.todos.forEach(todo => {
    if (todo.completed) {
      // 已完成的待办贡献100%进度
      totalProgress += 100
    } else {
      // 未完成的待办贡献其实际进度
      totalProgress += Math.min(todo.progress || 0, 100)
    }
  })
  
  // 计算平均进度，确保不超过100%
  const averageProgress = Math.round(totalProgress / totalCount)
  const finalProgress = Math.min(averageProgress, 100)
  
  // 调试信息
  console.log(`任务 "${task.name}" 进度计算:`, {
    totalCount,
    todos: task.todos.map(t => ({ title: t.title, progress: t.progress, completed: t.completed })),
    totalProgress,
    averageProgress,
    finalProgress
  })
  
  return finalProgress
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #48bb78, #38a169)' // 绿色
  if (progress >= 50) return 'linear-gradient(90deg, #ed8936, #dd6b20)' // 橙色
  if (progress >= 20) return 'linear-gradient(90deg, #ecc94b, #d69e2e)' // 黄色
  return 'linear-gradient(90deg, #e53e3e, #c53030)' // 红色
}

const getTotalTasks = () => {
  return topics.value.reduce((sum, topic) => sum + (topic.tasks ? topic.tasks.length : 0), 0)
}

const getOverallProgress = () => {
  if (!topics.value || topics.value.length === 0) return 0
  
  const totalProgress = topics.value.reduce((sum, topic) => {
    return sum + getTopicProgress(topic)
  }, 0)
  
  return Math.round(totalProgress / topics.value.length)
}

const getCompletedTodos = () => {
  if (!selectedTask.value || !selectedTask.value.todos) return 0
  return selectedTask.value.todos.filter(todo => todo.completed).length
}

// 删除功能
const deleteTopic = (topicId) => {
  if (confirm('确定要删除这个专题吗？这将删除专题下的所有任务和待办事项。')) {
    const index = topics.value.findIndex(t => t.id === topicId)
    if (index !== -1) {
      topics.value.splice(index, 1)
      if (selectedTopic.value && selectedTopic.value.id === topicId) {
        selectedTopic.value = null
        selectedTask.value = null
      }
      saveData()
    }
  }
}

const deleteTask = (taskId) => {
  if (confirm('确定要删除这个任务吗？这将删除任务下的所有待办事项。')) {
    const taskIndex = selectedTopic.value.tasks.findIndex(t => t.id === taskId)
    if (taskIndex !== -1) {
      selectedTopic.value.tasks.splice(taskIndex, 1)
      if (selectedTask.value && selectedTask.value.id === taskId) {
        selectedTask.value = null
      }
      saveData()
    }
  }
}

const deleteTodo = (todoId) => {
  if (confirm('确定要删除这个待办事项吗？')) {
    const todoIndex = selectedTask.value.todos.findIndex(t => t.id === todoId)
    if (todoIndex !== -1) {
      selectedTask.value.todos.splice(todoIndex, 1)
      saveData()
    }
  }
}

// 监听数据变化，自动保存
watch(topics, saveData, { deep: true })

// 数据导出为JSON
const exportUserData = () => {
  const userData = {
    username: currentUser.value.username,
    topics: topics.value
  }
  const dataStr = JSON.stringify(userData, null, 2)
  const blob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${currentUser.value.username}_todolist.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

// 数据导入
const importUserData = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const fileName = file.name.toLowerCase()
      
      if (fileName.endsWith('.json')) {
        // 导入JSON格式
        const importedData = JSON.parse(e.target.result)
        if (importedData.username === currentUser.value.username) {
          topics.value = importedData.topics
          alert('JSON数据导入成功！')
        } else {
          alert('导入数据与当前用户不匹配，请确保导入正确的数据文件。')
        }
      } else if (fileName.endsWith('.md') || fileName.endsWith('.txt')) {
        // 导入Markdown格式
        const markdownText = e.target.result
        
        // 验证Markdown格式
        const validation = validateMarkdownFormat(markdownText)
        if (!validation.valid) {
          alert('Markdown格式验证失败：\n' + validation.errors.join('\n'))
          return
        }
        
        // 解析Markdown为JSON
        const parsedTopics = parseMarkdownToJson(markdownText)
        topics.value = parsedTopics
        
        alert(`Markdown数据导入成功！\n解析出 ${parsedTopics.length} 个专题，共 ${parsedTopics.reduce((sum, t) => sum + t.tasks.length, 0)} 个任务`)
      } else {
        alert('不支持的文件格式，请使用 .json、.md 或 .txt 文件')
      }
      
      // 重置文件输入
      event.target.value = ''
      
    } catch (error) {
      alert('导入数据失败：' + error.message)
      console.error('导入数据失败:', error)
    }
  }
  reader.readAsText(file)
}

// 数据导出为Markdown
const exportUserDataAsMarkdown = () => {
  const markdownText = convertJsonToMarkdown(topics.value)
  const blob = new Blob([markdownText], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${currentUser.value.username}_todolist.md`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

// 数据清除
const clearUserData = () => {
  if (confirm('确定要清除所有数据吗？这将删除您的所有专题和任务。')) {
    localStorage.removeItem('currentUser')
    localStorage.removeItem('topics')
    currentUser.value = null
    topics.value = []
    selectedTopic.value = null
    selectedTask.value = null
    alert('数据已清除！')
  }
}
</script>

<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="nav-brand">TodoList 管理系统</div>
      <div class="nav-user" v-if="currentUser">
        <span>欢迎，{{ currentUser.username }}</span>
        <div class="user-actions">
          <input 
            type="file" 
            ref="fileInput" 
            @change="importUserData" 
            accept=".json,.md,.txt"
            style="display: none"
          />
          <button @click="$refs.fileInput.click()" class="btn-import">导入数据</button>
          <button @click="exportUserData" class="btn-export">导出JSON</button>
          <button @click="exportUserDataAsMarkdown" class="btn-export-md">导出MD</button>
          <button @click="clearUserData" class="btn-clear">清除数据</button>
          <button @click="logout" class="btn-logout">退出登录</button>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 登录注册页面 -->
      <div v-if="!currentUser" class="auth-container">
        <AuthForm @login="handleLogin" @register="handleRegister" />
      </div>

      <!-- 主应用内容 -->
      <div v-else class="app-container">
        <!-- 侧边栏导航 -->
        <aside class="sidebar">
          <div class="sidebar-header">
            <h3>我的专题</h3>
            <button @click="showCreateTopic = true" class="btn-primary">+ 新建专题</button>
          </div>
          
          <div class="topic-list">
            <div 
              v-for="topic in topics" 
              :key="topic.id"
              :class="['topic-item', { active: selectedTopic?.id === topic.id }]"
            >
              <div class="topic-content" @click="selectTopic(topic)">
                <div class="topic-info">
                  <h4>{{ topic.name }}</h4>
                  <div class="topic-progress">
                    <div class="progress-bar">
                      <div 
                        class="progress-fill" 
                        :style="{ 
                          width: getTopicProgress(topic) + '%',
                          background: getProgressColor(getTopicProgress(topic))
                        }"
                      ></div>
                    </div>
                    <span class="progress-text">{{ getTopicProgress(topic) }}%</span>
                  </div>
                </div>
              </div>
              <button 
                @click.stop="deleteTopic(topic.id)" 
                class="btn-delete"
                title="删除专题"
              >
                ×
              </button>
            </div>
          </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="content-area">
          <!-- 专题概览 -->
          <div v-if="!selectedTopic" class="overview">
            <h2>欢迎使用 TodoList</h2>
            
            <!-- 用户信息卡片 -->
            <div class="user-info-card">
              <div class="user-avatar">
                {{ currentUser.username.charAt(0).toUpperCase() }}
              </div>
              <div class="user-details">
                <h3>{{ currentUser.username }}</h3>
                <p>账户创建时间: {{ new Date(currentUser.createdAt || Date.now()).toLocaleDateString('zh-CN') }}</p>
                <p>总专题数: {{ topics.length }}</p>
              </div>
            </div>
            
            <div class="stats-grid">
              <div class="stat-card">
                <h3>总专题数</h3>
                <div class="stat-number">{{ topics.length }}</div>
              </div>
              <div class="stat-card">
                <h3>总任务数</h3>
                <div class="stat-number">{{ getTotalTasks() }}</div>
              </div>
              <div class="stat-card">
                <h3>完成率</h3>
                <div class="stat-number">{{ getOverallProgress() }}%</div>
              </div>
            </div>
            
            <!-- 专题完成情况饼图 -->
            <div class="chart-container">
              <h3>专题完成情况</h3>
              <div class="pie-chart">
                <canvas ref="pieChart" width="300" height="300"></canvas>
              </div>
            </div>
          </div>

          <!-- 专题详情 -->
          <div v-else class="topic-detail">
            <div class="topic-header">
              <button @click="selectedTopic = null" class="btn-back">← 返回概览</button>
              <h2>{{ selectedTopic.name }}</h2>
              <button @click="showCreateTask = true" class="btn-primary">+ 新建任务</button>
            </div>

            <!-- 任务列表 -->
            <div class="task-list">
              <div 
                v-for="task in selectedTopic.tasks" 
                :key="task.id"
                :class="['task-item', { active: selectedTask?.id === task.id }]"
              >
                <div class="task-content" @click="selectTask(task)">
                  <div class="task-info">
                    <h4>{{ task.name }}</h4>
                                      <div class="task-progress">
                    <div class="progress-bar">
                      <div 
                        class="progress-fill" 
                        :style="{ 
                          width: getTaskProgress(task) + '%',
                          background: getProgressColor(getTaskProgress(task))
                        }"
                      ></div>
                    </div>
                    <span class="progress-text">{{ getTaskProgress(task) }}%</span>
                  </div>
                  </div>
                </div>
                <button 
                  @click.stop="deleteTask(task.id)" 
                  class="btn-delete"
                  title="删除任务"
                >
                  ×
                </button>
              </div>
            </div>

            <!-- 任务详情 -->
            <div v-if="selectedTask" class="task-detail">
              <div class="task-header">
                <button @click="selectedTask = null" class="btn-back">← 返回任务列表</button>
                <h3>{{ selectedTask.name }}</h3>
                <button @click="showCreateTodo = true" class="btn-primary">+ 新建待办</button>
              </div>

              <!-- 待办清单 -->
              <div class="todo-list">
                <div 
                  v-for="todo in selectedTask.todos" 
                  :key="todo.id"
                  class="todo-item"
                >
                  <div class="todo-content">
                    <input 
                      type="checkbox" 
                      :checked="todo.completed"
                      @change="toggleTodo(todo)"
                    />
                    <span :class="{ completed: todo.completed }">{{ todo.title }}</span>
                  </div>
                  <div class="todo-actions">
                    <input 
                      v-model="todo.progress" 
                      type="range" 
                      min="0" 
                      max="100" 
                      class="progress-slider"
                      @input="updateTodoProgress(todo)"
                    />
                    <span class="progress-value">{{ todo.progress }}%</span>
                    <button @click="editTodo(todo)" class="btn-edit">编辑</button>
                    <button @click="deleteTodo(todo.id)" class="btn-delete-small">删除</button>
                  </div>
                  <div v-if="todo.note" class="todo-note">
                    备注: {{ todo.note }}
                  </div>
                </div>
              </div>

              <!-- 任务进度统计 -->
              <div class="task-stats">
                <h4>任务进度统计</h4>
                <div class="stats-row">
                  <div class="stat-item">
                    <span>总待办数:</span>
                    <span>{{ selectedTask.todos.length }}</span>
                  </div>
                  <div class="stat-item">
                    <span>已完成:</span>
                    <span>{{ getCompletedTodos() }}</span>
                  </div>
                  <div class="stat-item">
                    <span>完成率:</span>
                    <span>{{ getTaskProgress(selectedTask) }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 模态框 -->
    <CreateTopicModal 
      v-if="showCreateTopic" 
      @close="showCreateTopic = false"
      @create="createTopic"
    />
    
    <CreateTaskModal 
      v-if="showCreateTask" 
      @close="showCreateTask = false"
      @create="createTask"
    />
    
    <CreateTodoModal 
      v-if="showCreateTodo" 
      @close="showCreateTodo = false"
      @create="createTodo"
    />

    <EditTodoModal 
      v-if="editingTodo" 
      :todo="editingTodo"
      @close="editingTodo = null"
      @update="updateTodo"
    />
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-import {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-import:hover {
  background: rgba(255,255,255,0.3);
}

.btn-export, .btn-clear {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-export:hover, .btn-clear:hover {
  background: rgba(255,255,255,0.3);
}

.btn-export-md {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-export-md:hover {
  background: rgba(255,255,255,0.3);
}

.btn-logout {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-logout:hover {
  background: rgba(255,255,255,0.3);
}

.main-content {
  padding: 2rem;
}

.auth-container {
  max-width: 400px;
  margin: 4rem auto;
}

.app-container {
  display: flex;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.sidebar {
  width: 300px;
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  height: fit-content;
}

.sidebar-header {
  margin-bottom: 1.5rem;
}

.sidebar-header h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
}

.topic-item {
  padding: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 0.5rem;
  border: 1px solid #e2e8f0;
}

.topic-item:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.topic-item.active {
  background: #ebf8ff;
  border-color: #63b3ed;
}

.topic-item {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topic-content {
  flex: 1;
  cursor: pointer;
}

.task-item {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-content {
  flex: 1;
  cursor: pointer;
}

.topic-info h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.875rem;
  color: #718096;
}

.content-area {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.overview h2 {
  margin: 0 0 2rem 0;
  color: #2d3748;
}

.user-info-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  border: 1px solid #e2e8f0;
}

.user-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
}

.user-details h3 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.5rem;
}

.user-details p {
  margin: 0.25rem 0;
  color: #718096;
  font-size: 0.875rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
}

.stat-card h3 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  opacity: 0.9;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
}

.chart-container {
  margin-top: 2rem;
}

.chart-container h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
}

.pie-chart {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

.topic-header, .task-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.btn-back {
  background: #718096;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #4a5568;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.task-list {
  display: grid;
  gap: 1rem;
  margin-bottom: 2rem;
}

.task-item {
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.task-item:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.task-item.active {
  background: #ebf8ff;
  border-color: #63b3ed;
}

.task-info h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
}

.todo-list {
  margin-bottom: 2rem;
}

.todo-item {
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 1rem;
  background: #f7fafc;
}

.todo-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.todo-content input[type="checkbox"] {
  width: 18px;
  height: 18px;
}

.todo-content span {
  font-size: 1rem;
  color: #2d3748;
}

.todo-content span.completed {
  text-decoration: line-through;
  color: #718096;
}

.todo-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.progress-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
}

.progress-value {
  min-width: 50px;
  text-align: center;
  font-size: 0.875rem;
  color: #718096;
}

.btn-edit {
  background: #4299e1;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit:hover {
  background: #3182ce;
}

.btn-delete {
  background: #e53e3e;
  color: white;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.btn-delete:hover {
  background: #c53030;
  transform: scale(1.1);
}

.btn-delete-small {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
  transition: all 0.3s ease;
}

.btn-delete-small:hover {
  background: #c53030;
}

.todo-note {
  font-size: 0.875rem;
  color: #718096;
  font-style: italic;
  padding: 0.5rem;
  background: #edf2f7;
  border-radius: 4px;
}

.task-stats {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.task-stats h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.stat-item span:first-child {
  color: #718096;
}

.stat-item span:last-child {
  font-weight: 600;
  color: #2d3748;
}

@media (max-width: 768px) {
  .app-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
