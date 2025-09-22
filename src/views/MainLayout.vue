<template>
  <div class="main-layout">
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
          <button @click="goToTestPage" class="btn-test"> API测试</button>
          <button @click="logout" class="btn-logout">退出登录</button>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="app-container">
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
            @click="selectTopic(topic)"
          >
            <div class="topic-content">
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
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="content-area">
        <router-view />
      </main>
    </div>

    <!-- 模态框 -->
    <CreateTopicModal 
      v-if="showCreateTopic" 
      @close="showCreateTopic = false"
      @create="createTopic"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'
import { useTodoStore } from '../stores/useTodoStore.js'
import CreateTopicModal from '../components/CreateTopicModal.vue'
import { parseMarkdownToJson, convertJsonToMarkdown, validateMarkdownFormat } from '../utils/markdownParser.js'

const router = useRouter()
const todoStore = useTodoStore()

// 响应式数据
const currentUser = ref(null)
const showCreateTopic = ref(false)

// 从 store 获取数据
const topics = computed(() => todoStore.topics)
const selectedTopic = computed(() => todoStore.selectedTopic)

// 初始化数据
onMounted(async () => {
  console.log('🎯 MainLayout 应用启动')
  
  // 检查认证状态
  const isAuthenticated = authService.isAuthenticated()
  
  if (isAuthenticated) {
    currentUser.value = authService.getCurrentUser()
    console.log('用户已认证:', currentUser.value)
    
    // 设置用户到 store
    todoStore.setCurrentUser(currentUser.value)
    
    // 从数据库加载用户专题数据
    try {
      await todoStore.loadUserTopics(currentUser.value.id)
    } catch (error) {
      console.error('加载用户数据失败:', error)
      // 如果数据库加载失败，可以回退到本地存储
      // todoStore.loadUserTopicsFromLocal(currentUser.value.username)
    }
  } else {
    console.log('用户未认证，重定向到登录页')
    router.push('/login')
  }
})

// 用户认证
const logout = async () => {
  try {
    await authService.logout()
    console.log('退出登录成功')
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    currentUser.value = null
    todoStore.clearData()
    router.push('/login')
  }
}

// 专题管理
const createTopic = async (topicData) => {
  try {
    const uniqueName = checkTopicNameConflict(topicData.name)
    
    const newTopic = await todoStore.createTopic({
      name: uniqueName,
      description: topicData.description
    })
    
    showCreateTopic.value = false
    
    // 如果名称被修改，提示用户
    if (uniqueName !== topicData.name) {
      alert(`名称已存在，专题名称已自动重命名为：${uniqueName}`)
    }
  } catch (error) {
    console.error('创建专题失败:', error)
    alert('创建专题失败：' + error.message)
  }
}

// 选择专题
const selectTopic = (topic) => {
  todoStore.selectedTopic = { ...topic }
  todoStore.selectedTask = null
  // 导航到专题详情页面
  router.push(`/topic/${topic.id}`)
}

// 重名检测和自动重命名工具函数
const generateUniqueName = (baseName, existingNames, type = '') => {
  if (!existingNames.includes(baseName)) {
    return baseName
  }
  
  let counter = 1
  let newName = `${baseName}_${counter}`
  
  while (existingNames.includes(newName)) {
    counter++
    newName = `${baseName}_${counter}`
  }
  
  return newName
}

// 检测专题重名
const checkTopicNameConflict = (name, excludeId = null) => {
  const existingNames = topics.value
    .filter(topic => topic.id !== excludeId)
    .map(topic => topic.name)
  return generateUniqueName(name, existingNames, '专题')
}

// 进度计算
const getTopicProgress = (topic) => {
  console.log('计算专题进度:', topic.name, topic.tasks)
  
  if (!topic.tasks || topic.tasks.length === 0) {
    console.log('专题没有任务，进度为0')
    return 0
  }
  
  const totalProgress = topic.tasks.reduce((sum, task) => {
    const taskProgress = getTaskProgress(task)
    console.log(`任务 "${task.title}" 进度:`, taskProgress)
    return sum + taskProgress
  }, 0)
  
  const finalProgress = Math.round(totalProgress / topic.tasks.length)
  console.log(`专题 "${topic.name}" 最终进度:`, finalProgress)
  return finalProgress
}

const getTaskProgress = (task) => {
  console.log('计算任务进度:', task.title, task.todos)
  
  if (!task.todos || task.todos.length === 0) {
    console.log('任务没有待办事项，进度为0')
    return 0
  }
  
  const totalCount = task.todos.length
  let totalProgress = 0
  
  task.todos.forEach(todo => {
    const isCompleted = todo.isCompleted || todo.completed
    const progress = todo.progress || 0
    console.log(`待办 "${todo.title}" 完成状态:`, isCompleted, '进度:', progress)
    
    if (isCompleted) {
      totalProgress += 100
    } else {
      totalProgress += Math.min(progress, 100)
    }
  })
  
  const averageProgress = Math.round(totalProgress / totalCount)
  const finalProgress = Math.min(averageProgress, 100)
  console.log(`任务 "${task.title}" 最终进度:`, finalProgress)
  return finalProgress
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #48bb78, #38a169)'
  if (progress >= 50) return 'linear-gradient(90deg, #ed8936, #dd6b20)'
  if (progress >= 20) return 'linear-gradient(90deg, #ecc94b, #d69e2e)'
  return 'linear-gradient(90deg, #e53e3e, #c53030)'
}

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
        const importedData = JSON.parse(e.target.result)
        if (importedData.username === currentUser.value.username) {
          // 这里可以实现批量导入到数据库的逻辑
          alert('JSON数据导入功能需要进一步实现')
        } else {
          alert('导入数据与当前用户不匹配，请确保导入正确的数据文件。')
        }
      } else if (fileName.endsWith('.md') || fileName.endsWith('.txt')) {
        const markdownText = e.target.result
        const validation = validateMarkdownFormat(markdownText)
        if (!validation.valid) {
          alert('Markdown格式验证失败：\n' + validation.errors.join('\n'))
          return
        }
        
        // 这里可以实现Markdown导入到数据库的逻辑
        alert('Markdown数据导入功能需要进一步实现')
      } else {
        alert('不支持的文件格式，请使用 .json、.md 或 .txt 文件')
      }
      
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
    // 这里可以实现批量删除数据库数据的逻辑
    alert('数据清除功能需要进一步实现')
  }
}

// 在 MainLayout.vue 的 script 部分添加
const goToTestPage = () => {
  router.push('/test')
}
</script>

<style scoped>
.main-layout {
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

.btn-import, .btn-export, .btn-export-md, .btn-clear, .btn-logout {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-import:hover, .btn-export:hover, .btn-export-md:hover, .btn-clear:hover, .btn-logout:hover {
  background: rgba(255,255,255,0.3);
}

.app-container {
  display: flex;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
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
  position: relative;
  display: block;
}

.topic-item:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.topic-item.active {
  background: #ebf8ff;
  border-color: #63b3ed;
  box-shadow: 0 2px 8px rgba(99, 179, 237, 0.2);
}

.topic-content {
  width: 100%;
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
</style>