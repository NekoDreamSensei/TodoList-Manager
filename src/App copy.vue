<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import AuthForm from './components/AuthForm.vue'
import CreateTopicModal from './components/CreateTopicModal.vue'
import CreateTaskModal from './components/CreateTaskModal.vue'
import CreateTodoModal from './components/CreateTodoModal.vue'
import EditTodoModal from './components/EditTodoModal.vue'
import EditTopicModal from './components/EditTopicModal.vue'
import ProgressOverview from './components/ProgressOverview.vue'
import EditTaskModal from './components/EditTaskModal.vue'
import { parseMarkdownToJson, convertJsonToMarkdown, validateMarkdownFormat } from './utils/markdownParser.js'
import ExportViewModal from './components/ExportViewModal.vue'
import { userService } from './services/userService.js'

// 导入认证服务
import { authService } from './services/authService.js'
import { autoLoginCheck } from './utils/authGuard.js'
import systemChecker from './utils/systemCheck.js'

// 响应式数据
const currentUser = ref(null)
const topics = ref([])
const selectedTopic = ref(null)
const selectedTask = ref(null)
const showCreateTopic = ref(false)
const showCreateTask = ref(false)
const showCreateTodo = ref(false)
const editingTodo = ref(null)
const showEditTopic = ref(false)
const showEditTask = ref(false)
const selectedTaskForEdit = ref(null)
const showExportView = ref(false)

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

// 检测任务重名
const checkTaskNameConflict = (name, topicId, excludeId = null) => {
  const topic = topics.value.find(t => t.id === topicId)
  if (!topic) return name
  
  const existingNames = topic.tasks
    .filter(task => task.id !== excludeId)
    .map(task => task.name)
  return generateUniqueName(name, existingNames, '任务')
}

// 检测待办重名
const checkTodoNameConflict = (name, topicId, taskId, excludeId = null) => {
  const topic = topics.value.find(t => t.id === topicId)
  if (!topic) return name
  
  const task = topic.tasks.find(t => t.id === taskId)
  if (!task) return name
  
  const existingNames = task.todos
    .filter(todo => todo.id !== excludeId)
    .map(todo => todo.title)
  return generateUniqueName(name, existingNames, '待办')
}

// 初始化数据
onMounted(async () => {
  console.log('�� 应用启动中...')
  
  console.log('🎯 TodoList 应用启动')
  console.log('📅 启动时间:', new Date().toLocaleString())
  console.log('🌍 环境:', import.meta.env.MODE)
  console.log('🔗 API地址:', import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080')
  
  // 运行系统检查
  await systemChecker.runAllChecks()
  
  // 加载数据
  await loadData()
  
  console.log('✅ 应用启动完成')
})

// 加载数据
const loadData = async () => {
  console.log('loadData被调用')
  
  // 检查认证状态
  const isAuthenticated = await autoLoginCheck()
  
  if (isAuthenticated) {
    currentUser.value = authService.getCurrentUser()
    console.log('用户已认证:', currentUser.value)
    // 加载当前用户的专题数据
    loadUserTopics()
  } else {
    console.log('用户未认证或认证已过期')
    currentUser.value = null
  }
}

// 加载用户专题数据
const loadUserTopics = () => {
  if (!currentUser.value) return
  
  const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
  const userTopics = allTopics[currentUser.value.username] || []
  topics.value = userTopics
  
  // 调试信息
  console.log(`加载用户 ${currentUser.value.username} 的数据:`, {
    topicsCount: userTopics.length,
    totalTasks: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.length : 0), 0),
    totalTodos: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.reduce((s, task) => s + (task.todos ? task.todos.length : 0), 0) : 0), 0),
    localStorageKeys: Object.keys(allTopics),
    allTopicsData: allTopics
  })
}

// 保存数据
const saveData = () => {
  if (!currentUser.value) return
  
  const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
  allTopics[currentUser.value.username] = topics.value
  localStorage.setItem('topics', JSON.stringify(allTopics))
  
  // 调试信息
  console.log(`保存用户 ${currentUser.value.username} 的数据:`, {
    topicsCount: topics.value.length,
    totalTasks: topics.value.reduce((sum, t) => sum + (t.tasks ? t.tasks.length : 0), 0),
    totalTodos: topics.value.reduce((sum, t) => sum + (t.tasks ? t.tasks.reduce((s, task) => s + (task.todos ? task.todos.length : 0), 0) : 0), 0),
    localStorageSize: JSON.stringify(allTopics).length
  })
}

// 用户认证
const handleLogin = (user) => {
  currentUser.value = user
  // 登录后加载用户数据
  loadUserTopics()
}

const handleRegister = (user) => {
  currentUser.value = user
  // 注册后初始化用户数据
  topics.value = []
  saveData()
}

const logout = async () => {
  try {
    // 使用认证服务退出登录
    await authService.logout()
    console.log('退出登录成功')
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    // 清除本地状态
    currentUser.value = null
    selectedTopic.value = null
    selectedTask.value = null
    topics.value = []
    console.log('本地状态已清除')
  }
}

// 专题管理
const createTopic = (topicData) => {
  const uniqueName = checkTopicNameConflict(topicData.name)
  
  const newTopic = {
    id: Date.now().toString(),
    name: uniqueName,
    description: topicData.description,
    createdAt: new Date().toISOString(),
    tasks: []
  }
  
  topics.value.push(newTopic)
  saveData()
  showCreateTopic.value = false
  
  // 如果名称被修改，提示用户
  if (uniqueName !== topicData.name) {
    alert(`名称已存在，专题名称已自动重命名为：${uniqueName}`)
  }
}

// 选择专题
const selectTopic = (topic) => {
  selectedTopic.value = { ...topic } // 创建副本而不是直接引用
  selectedTask.value = null
}

// 任务管理
const createTask = (taskData) => {
  if (!selectedTopic.value) return
  
  const uniqueName = checkTaskNameConflict(taskData.name, selectedTopic.value.id)
  
  const newTask = {
    id: Date.now().toString(),
    name: uniqueName,
    description: taskData.description,
    createdAt: new Date().toISOString(),
    todos: []
  }
  
  selectedTopic.value.tasks.push(newTask)
  saveData()
  showCreateTask.value = false
  
  // 如果名称被修改，提示用户
  if (uniqueName !== taskData.name) {
    alert(`名称已存在，任务名称已自动重命名为：${uniqueName}`)
  }
}

const selectTask = (task) => {
  selectedTask.value = task
}

// 待办管理
const createTodo = (todoData) => {
  if (!selectedTask.value || !selectedTopic.value) return
  
  const uniqueName = checkTodoNameConflict(todoData.title, selectedTopic.value.id, selectedTask.value.id)
  
  const newTodo = {
    id: Date.now().toString(),
    title: uniqueName,
    description: '', // 保持空字符串，因为不再使用描述字段
    completed: todoData.progress === 100, // 如果进度是100%，则自动标记为完成
    progress: todoData.progress, // 使用用户设置的进度
    note: todoData.note, // 使用用户设置的备注
    createdAt: new Date().toISOString()
  }
  
  selectedTask.value.todos.push(newTodo)
  saveData()
  showCreateTodo.value = false
  
  // 如果名称被修改，提示用户
  if (uniqueName !== todoData.title) {
    alert(`名称已存在，待办名称已自动重命名为：${uniqueName}`)
  }
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
  if (!selectedTask.value || !selectedTopic.value) return
  
  const todoIndex = selectedTask.value.todos.findIndex(todo => todo.id === updatedTodo.id)
  if (todoIndex !== -1) {
    const uniqueName = checkTodoNameConflict(updatedTodo.title, selectedTopic.value.id, selectedTask.value.id, updatedTodo.id)
    
    selectedTask.value.todos[todoIndex] = { 
      ...updatedTodo, 
      title: uniqueName 
    }
    
    saveData()
    editingTodo.value = null
    
    // 如果名称被修改，提示用户
    if (uniqueName !== updatedTodo.title) {
      alert(`名称已存在，待办名称已自动重命名为：${uniqueName}`)
    } else {
      alert('待办信息已更新！')
    }
  }
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
watch(topics, () => {
  if (currentUser.value) {
    saveData()
  }
}, { deep: true })

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
          // 合并数据而不是覆盖
          const mergedTopics = mergeTopics(topics.value, importedData.topics)
          topics.value = mergedTopics
          // 保存到本地存储
          saveData()
          alert(`JSON数据导入成功！\n合并后共有 ${mergedTopics.length} 个专题`)
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
        
        // 合并数据而不是覆盖
        const mergedTopics = mergeTopics(topics.value, parsedTopics)
        topics.value = mergedTopics
        
        // 保存到本地存储
        saveData()
        
        alert(`Markdown数据导入成功！\n解析出 ${parsedTopics.length} 个专题，合并后共有 ${mergedTopics.length} 个专题`)
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

// 合并专题数据
const mergeTopics = (existingTopics, newTopics) => {
  if (!newTopics || newTopics.length === 0) {
    return existingTopics
  }
  
  if (!existingTopics || existingTopics.length === 0) {
    return newTopics
  }
  
  const merged = [...existingTopics]
  const existingNames = new Set(existingTopics.map(t => t.name))
  
  newTopics.forEach(newTopic => {
    if (existingNames.has(newTopic.name)) {
      // 如果专题名称已存在，合并任务
      const existingTopic = existingTopics.find(t => t.name === newTopic.name)
      if (existingTopic) {
        const mergedTasks = mergeTasks(existingTopic.tasks, newTopic.tasks)
        existingTopic.tasks = mergedTasks
        existingTopic.description = newTopic.description || existingTopic.description
      }
    } else {
      // 如果专题名称不存在，直接添加
      merged.push({
        ...newTopic,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9) // 生成新的唯一ID
      })
    }
  })
  
  return merged
}

// 合并任务数据
const mergeTasks = (existingTasks, newTasks) => {
  if (!newTasks || newTasks.length === 0) {
    return existingTasks
  }
  
  if (!existingTasks || existingTasks.length === 0) {
    return newTasks
  }
  
  const merged = [...existingTasks]
  const existingNames = new Set(existingTasks.map(t => t.name))
  
  newTasks.forEach(newTask => {
    if (existingNames.has(newTask.name)) {
      // 如果任务名称已存在，合并待办事项
      const existingTask = existingTasks.find(t => t.name === newTask.name)
      if (existingTask) {
        const mergedTodos = mergeTodos(existingTask.todos, newTask.todos)
        existingTask.todos = mergedTodos
        existingTask.description = newTask.description || existingTask.description
      }
    } else {
      // 如果任务名称不存在，直接添加
      merged.push({
        ...newTask,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9) // 生成新的唯一ID
      })
    }
  })
  
  return merged
}

// 合并待办事项数据
const mergeTodos = (existingTodos, newTodos) => {
  if (!newTodos || newTodos.length === 0) {
    return existingTodos
  }
  
  if (!existingTodos || existingTodos.length === 0) {
    return newTodos
  }
  
  const merged = [...existingTodos]
  const existingTitles = new Set(existingTodos.map(t => t.title))
  
  newTodos.forEach(newTodo => {
    if (existingTitles.has(newTodo.title)) {
      // 如果待办标题已存在，更新进度和备注（保留较高的进度）
      const existingTodo = existingTodos.find(t => t.title === newTodo.title)
      if (existingTodo) {
        existingTodo.progress = Math.max(existingTodo.progress || 0, newTodo.progress || 0)
        existingTodo.note = newTodo.note || existingTodo.note
        existingTodo.description = newTodo.description || existingTodo.description
        existingTodo.completed = existingTodo.progress === 100 || newTodo.completed
      }
    } else {
      // 如果待办标题不存在，直接添加
      merged.push({
        ...newTodo,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9) // 生成新的唯一ID
      })
    }
  })
  
  return merged
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

// 更新专题信息
const updateTopic = (updatedTopic) => {
  const index = topics.value.findIndex(topic => topic.id === updatedTopic.id)
  if (index !== -1) {
    const uniqueName = checkTopicNameConflict(updatedTopic.name, updatedTopic.id)
    
    topics.value[index] = { 
      ...updatedTopic, 
      name: uniqueName 
    }
    
    // 同步更新当前选中的专题
    if (selectedTopic.value && selectedTopic.value.id === updatedTopic.id) {
      selectedTopic.value = { ...topics.value[index] }
    }
    
    saveData()
    
    // 如果名称被修改，提示用户
    if (uniqueName !== updatedTopic.name) {
      alert(`名称已存在，专题名称已自动重命名为：${uniqueName}`)
    } else {
      alert('专题信息已更新！')
    }
  }
}

// 删除专题
const deleteTopicFromEdit = (topicId) => {
  const index = topics.value.findIndex(topic => topic.id === topicId)
  if (index !== -1) {
    topics.value.splice(index, 1)
    saveData()
    selectedTopic.value = null
    alert('专题已删除！')
  }
}

// 切换任务详情显示/隐藏
const toggleTaskDetail = (task) => {
  if (selectedTask.value && selectedTask.value.id === task.id) {
    // 如果当前任务已选中，则取消选中（折叠）
    selectedTask.value = null
  } else {
    // 否则选中该任务（展开）
    selectTask(task)
  }
}

// 更新任务信息
const updateTask = (updatedTask) => {
  if (selectedTopic.value) {
    const taskIndex = selectedTopic.value.tasks.findIndex(task => task.id === updatedTask.id)
    if (taskIndex !== -1) {
      const uniqueName = checkTaskNameConflict(updatedTask.name, selectedTopic.value.id, updatedTask.id)
      
      selectedTopic.value.tasks[taskIndex] = { 
        ...updatedTask, 
        name: uniqueName 
      }
      
      // 同步更新当前选中的任务
      if (selectedTask.value && selectedTask.value.id === updatedTask.id) {
        selectedTask.value = { ...selectedTopic.value.tasks[taskIndex] }
      }
      
      saveData()
      showEditTask.value = false
      
      // 如果名称被修改，提示用户
      if (uniqueName !== updatedTask.name) {
        alert(`名称已存在，任务名称已自动重命名为：${uniqueName}`)
      } else {
        alert('任务信息已更新！')
      }
    }
  }
}

// 删除任务
const deleteTaskFromEdit = (taskId) => {
  if (selectedTopic.value) {
    const taskIndex = selectedTopic.value.tasks.findIndex(task => task.id === taskId)
    if (taskIndex !== -1) {
      selectedTopic.value.tasks.splice(taskIndex, 1)
      selectedTask.value = null
      saveData()
      alert('任务已删除！')
    }
  }
}

</script>

<template>
  <div id="app">
    <!-- 系统状态组件（仅开发环境显示） -->
    <SystemStatus v-if="isDev" />
    
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
          <button @click="showExportView = true" class="btn-export-view">导出视图</button>
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
            </div>
          </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="content-area">
          <!-- 专题概览 -->
          <div v-if="!selectedTopic" class="main-overview">
            <ProgressOverview :topics="topics" />
          </div>

          <!-- 专题详情 -->
          <div v-else class="topic-detail">
            <div class="topic-header">
              <button @click="selectedTopic = null" class="btn-back">← 返回概览</button>
              <h2>{{ selectedTopic.name }}</h2>
              <div class="header-actions">
                <button @click="showEditTopic = true" class="btn-manage">管理专题</button>
                <button @click="showCreateTask = true" class="btn-primary">+ 新建任务</button>
              </div>
            </div>

            <!-- 专题描述 -->
            <div v-if="selectedTopic.description" class="topic-description">
              <h4>专题描述</h4>
              <p>{{ selectedTopic.description }}</p>
            </div>

            <!-- 任务列表 -->
            <div class="task-list">
              <div 
                v-for="task in selectedTopic.tasks" 
                :key="task.id"
                class="task-container"
              >
                <!-- 任务项 -->
                <div 
                  :class="['task-item', { active: selectedTask?.id === task.id }]"
                >
                  <div class="task-content" @click="toggleTaskDetail(task)">
                    <div class="task-info">
                      <h4>{{ task.name }}</h4>
                      <!-- 任务描述预览 -->
                      <div v-if="task.description" class="task-description-preview">
                        {{ task.description.length > 50 ? task.description.substring(0, 50) + '...' : task.description }}
                      </div>
                    </div>
                    <div class="task-progress-section">
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
                      <button 
                        @click.stop="showEditTask = true; selectedTaskForEdit = task" 
                        class="btn-manage-task"
                        title="管理任务"
                      >
                        管理
                      </button>
                    </div>
                  </div>
                </div>

                <!-- 任务详情（展开在任务下方） -->
                <div v-if="selectedTask && selectedTask.id === task.id" class="task-detail-expanded">
                  <div class="task-detail-header">
                    <h3>{{ selectedTask.name }}</h3>
                    <button @click="showCreateTodo = true" class="btn-primary">+ 新建待办</button>
                  </div>

                  <!-- 任务描述 -->
                  <div v-if="selectedTask.description" class="task-description">
                    <h4>任务描述</h4>
                    <p>{{ selectedTask.description }}</p>
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
        </div>
      </div>
  </main>

    <!-- 模态框 -->
    <CreateTopicModal 
      v-if="showCreateTopic" 
      @close="showCreateTopic = false"
      @create="createTopic"
    />
    
    <EditTopicModal 
      v-if="showEditTopic && selectedTopic" 
      :topic="selectedTopic"
      @close="showEditTopic = false"
      @update="updateTopic"
      @delete="deleteTopicFromEdit"
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

    <EditTaskModal 
      v-if="showEditTask && selectedTaskForEdit" 
      :task="selectedTaskForEdit"
      @close="showEditTask = false; selectedTaskForEdit = null"
      @update="updateTask"
      @delete="deleteTaskFromEdit"
    />

    <ExportViewModal
      v-if="showExportView"
      :topics="topics"
      :username="currentUser.username"
      @close="showExportView = false"
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

.btn-nav {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-nav:hover {
  background: rgba(255,255,255,0.3);
}

.btn-nav.active {
  background: rgba(255,255,255,0.4);
  border-color: rgba(255,255,255,0.6);
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

.btn-export-view {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-export-view:hover {
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

/* 概览页面样式 */
.overview-container {
  width: 100%;
  min-height: 100vh;
  padding: 20px;
  background: #f8f9fa;
}

/* 主界面右侧概览样式 */
.main-overview {
  height: calc(100vh - 120px);
  overflow-y: auto;
  padding: 0;
  margin: -2rem;
  background: #f8f9fa;
  border-radius: 12px;
}

/* 导航按钮样式 */
.btn-nav {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-nav:hover {
  background: rgba(255,255,255,0.3);
}

.btn-nav.active {
  background: rgba(255,255,255,0.4);
  border-color: rgba(255,255,255,0.6);
}

/* 专题描述样式 */
.topic-description {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  border-left: 4px solid #007bff;
}

.topic-description h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.topic-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

/* 任务描述预览样式 */
.task-description-preview {
  color: #666;
  font-size: 12px;
  margin: 5px 0;
  line-height: 1.4;
  font-style: italic;
}

/* 任务描述样式 */
.task-description {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  border-left: 4px solid #28a745;
}

.task-description h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.task-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

/* 待办备注样式优化 */
.todo-note {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  padding: 8px 12px;
  margin-top: 8px;
  font-size: 12px;
  color: #856404;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .topic-description,
  .task-description {
    padding: 12px;
    margin-bottom: 15px;
  }
  
  .topic-description h4,
  .task-description h4 {
    font-size: 14px;
  }
  
  .topic-description p,
  .task-description p {
    font-size: 13px;
  }
  
  .task-description-preview {
    font-size: 11px;
  }
}

/* 专题头部样式优化 */
.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e9ecef;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-manage {
  background: #6c757d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-manage:hover {
  background: #545b62;
  transform: translateY(-1px);
}

.btn-back {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #dee2e6;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.btn-back:hover {
  background: #e9ecef;
  color: #495057;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .topic-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .header-actions {
    justify-content: space-between;
  }
  
  .btn-manage,
  .btn-back {
    flex: 1;
  }
}

/* 任务容器样式 */
.task-container {
  margin-bottom: 20px;
}

/* 任务详情展开区域 */
.task-detail-expanded {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-top: none;
  border-radius: 0 0 8px 8px;
  padding: 20px;
  margin-top: -1px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.task-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e9ecef;
}

.task-detail-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

/* 任务项激活状态优化 */
.task-item.active {
  background: #ebf8ff;
  border-color: #63b3ed;
  border-radius: 8px 8px 0 0;
  border-bottom: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-detail-expanded {
    padding: 15px;
  }
  
  .task-detail-header {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .task-detail-header h3 {
    font-size: 18px;
  }
}

/* 任务项布局优化 */
.task-item {
  padding: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 0.5rem;
  border: 1px solid #e2e8f0;
  position: relative;
  display: flex;
  align-items: center;
}

.task-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  cursor: pointer;
}

.task-info {
  flex: 1;
  min-width: 0;
}

.task-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.3;
}

.task-progress-section {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 120px;
}

.progress-bar {
  width: 80px;
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #6c757d;
  min-width: 35px;
  text-align: right;
}

.btn-manage-task {
  background: #6c757d;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-manage-task:hover {
  background: #545b62;
  transform: translateY(-1px);
}

.task-description-preview {
  color: #666;
  font-size: 12px;
  margin: 5px 0 0 0;
  line-height: 1.4;
  font-style: italic;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 任务项悬停和激活状态 */
.task-item:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.task-item.active {
  background: #ebf8ff;
  border-color: #63b3ed;
  border-radius: 8px 8px 0 0;
  border-bottom: none;
  box-shadow: 0 2px 8px rgba(99, 179, 237, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-content {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .task-progress-section {
    justify-content: space-between;
  }
  
  .task-progress {
    min-width: auto;
  }
  
  .progress-bar {
    width: 60px;
  }
  
  .btn-manage-task {
    padding: 8px 16px;
    font-size: 14px;
  }
}

</style>
