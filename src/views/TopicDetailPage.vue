<template>
  <div class="topic-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>正在加载专题详情...</p>
    </div>

    <!-- 专题不存在 -->
    <div v-else-if="!selectedTopic" class="error-state">
      <h3>专题不存在</h3>
      <p>抱歉，您访问的专题不存在或已被删除。</p>
      <button @click="goBack" class="btn-primary">返回概览</button>
    </div>

    <!-- 专题详情内容 -->
    <div v-else class="topic-detail">
      <!-- 专题头部 -->
      <div class="topic-header">
        <button @click="goBack" class="btn-back">← 返回概览</button>
        <h2>{{ selectedTopic?.name || '专题详情' }}</h2>
        <div class="header-actions">
          <button @click="showEditTopic = true" class="btn-manage">管理专题</button>
          <button @click="showCreateTask = true" class="btn-primary">+ 新建任务</button>
        </div>
      </div>

      <!-- 专题进度总览 -->
      <div class="topic-progress-overview">
        <div class="progress-card">
          <h4>专题进度总览</h4>
          <div class="progress-bar-large">
            <div 
              class="progress-fill-large" 
              :style="{ 
                width: getTopicProgress(selectedTopic) + '%',
                background: getProgressColor(getTopicProgress(selectedTopic))
              }"
            ></div>
          </div>
          <div class="progress-stats">
            <span class="progress-percentage">{{ getTopicProgress(selectedTopic) }}%</span>
            <span class="progress-details">
              {{ selectedTopic.tasks?.length || 0 }} 个任务
            </span>
          </div>
        </div>
      </div>

      <!-- 专题描述 -->
      <div v-if="selectedTopic?.description" class="topic-description">
        <h4>专题描述</h4>
        <p>{{ selectedTopic.description }}</p>
      </div>

      <!-- 任务列表 -->
      <div class="task-list">
        <div v-if="selectedTopic?.tasks?.length === 0" class="empty-state">
          <div class="empty-icon"></div>
          <h3>还没有创建任何任务</h3>
          <p>点击"新建任务"按钮开始创建您的第一个任务吧！</p>
        </div>
        <div 
          v-for="task in selectedTopic?.tasks" 
          :key="task.id"
          class="task-container"
        >
          <!-- 任务项 -->
          <div 
            :class="['task-item', { active: selectedTask?.id === task.id }]"
          >
            <div class="task-content" @click="toggleTaskDetail(task)">
              <div class="task-info">
                <h4>{{ task.title }}</h4>
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
                  @click.stop="editTask(task)" 
                  class="btn-manage-task"
                  title="管理任务"
                >
                  管理
                </button>
              </div>
            </div>
          </div>

          <!-- 任务详情（展开状态） -->
          <div v-if="selectedTask && selectedTask.id === task.id" class="task-detail-expanded">
            <div class="task-detail-header">
              <h3>{{ selectedTask.title }}</h3>
              <button @click="showCreateTodo = true" class="btn-primary">+ 新建待办</button>
            </div>

            <!-- 任务进度总览 -->
            <div class="task-progress-overview">
              <div class="progress-card-small">
                <h5>任务进度</h5>
                <div class="progress-bar-medium">
                  <div 
                    class="progress-fill-medium" 
                    :style="{ 
                      width: getTaskProgress(selectedTask) + '%',
                      background: getProgressColor(getTaskProgress(selectedTask))
                    }"
                  ></div>
                </div>
                <div class="progress-stats-small">
                  <span>{{ getTaskProgress(selectedTask) }}%</span>
                  <span>{{ getCompletedTodos(selectedTask) }}/{{ selectedTask.todos?.length || 0 }} 已完成</span>
                </div>
              </div>
            </div>

            <!-- 任务描述 -->
            <div v-if="selectedTask.description" class="task-description">
              <h4>任务描述</h4>
              <p>{{ selectedTask.description }}</p>
            </div>

            <!-- 待办清单 -->
            <div class="todo-list">
              <div v-if="!selectedTask.todos || selectedTask.todos.length === 0" class="empty-todos">
                <div class="empty-icon">✅</div>
                <h4>还没有创建任何待办事项</h4>
                <p>点击"新建待办"按钮开始创建吧！</p>
              </div>
              <div 
                v-for="todo in selectedTask.todos" 
                :key="todo.id"
                :class="['todo-item', { completed: todo.isCompleted || todo.completed }]"
              >
                <div class="todo-content">
                  <input 
                    type="checkbox" 
                    :checked="todo.isCompleted || todo.completed"
                    @change="toggleTodoComplete(todo)"
                    class="todo-checkbox"
                  >
                  <div class="todo-info">
                    <span class="todo-text">{{ todo.title }}</span>
                    <div v-if="todo.content" class="todo-description">{{ todo.content }}</div>
                    <div class="todo-meta">
                      <span class="todo-priority">{{ todo.priority || '普通' }}</span>
                      <span class="todo-time">{{ formatTime(todo.createdAt) }}</span>
                    </div>
                  </div>
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
                  <span class="progress-value">{{ todo.progress || 0 }}%</span>
                  <button @click="editTodo(todo)" class="btn-edit">编辑</button>
                  <button @click.stop="deleteTodo(todo.id)" class="btn-delete-small">删除</button>
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
                  <span>{{ selectedTask.todos?.length || 0 }}</span>
                </div>
                <div class="stat-item">
                  <span>已完成:</span>
                  <span>{{ getCompletedTodos(selectedTask) }}</span>
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

    <!-- 模态框组件 -->
    <EditTopicModal 
      v-if="showEditTopic && selectedTopic" 
      :topic="selectedTopic"
      @close="showEditTopic = false"
      @update="handleTopicUpdate"
      @delete="handleTopicDelete"
    />
    
    <CreateTaskModal 
      v-if="showCreateTask" 
      @close="showCreateTask = false"
      @create="handleTaskCreate"
    />
    
    <EditTaskModal 
      v-if="showEditTask && selectedTaskForEdit" 
      :task="selectedTaskForEdit"
      @close="showEditTask = false"
      @update="handleTaskUpdate"
      @delete="handleTaskDelete"
    />
    
    <CreateTodoModal 
      v-if="showCreateTodo" 
      @close="showCreateTodo = false"
      @create="handleTodoCreate"
    />
    
    <EditTodoModal 
      v-if="editingTodo" 
      :todo="editingTodo"
      @close="editingTodo = null"
      @update="handleTodoUpdate"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTodoStore } from '../stores/useTodoStore.js'
import { topicService } from '../services/topicService.js'
import { taskService } from '../services/taskService.js'
import { todoService } from '../services/todoService.js'
import EditTopicModal from '../components/EditTopicModal.vue'
import CreateTaskModal from '../components/CreateTaskModal.vue'
import EditTaskModal from '../components/EditTaskModal.vue'
import CreateTodoModal from '../components/CreateTodoModal.vue'
import EditTodoModal from '../components/EditTodoModal.vue'

const route = useRoute()
const router = useRouter()
const todoStore = useTodoStore()

// 响应式数据
const loading = ref(false)
const showEditTopic = ref(false)
const showCreateTask = ref(false)
const showCreateTodo = ref(false)
const editingTodo = ref(null)
const showEditTask = ref(false)
const selectedTaskForEdit = ref(null)

// 从 store 获取数据
const selectedTopic = computed(() => {
  console.log('计算 selectedTopic:', todoStore.selectedTopic)
  return todoStore.selectedTopic
})

const selectedTask = computed(() => {
  console.log('计算 selectedTask:', todoStore.selectedTask)
  return todoStore.selectedTask
})

// 初始化
onMounted(async () => {
  console.log(' TopicDetailPage 页面加载')
  
  // 从路由参数获取专题ID
  const topicId = route.params.id
  if (topicId) {
    await loadTopicDetail(topicId)
  } else {
    // 如果没有专题ID，返回概览页
    router.push('/dashboard')
  }
})

// 监听路由参数变化
watch(() => route.params.id, async (newTopicId) => {
  if (newTopicId) {
    await loadTopicDetail(newTopicId)
  }
})

// 加载专题详情
const loadTopicDetail = async (topicId) => {
  loading.value = true
  
  try {
    console.log('正在加载专题详情:', topicId)
    
    // 从后端加载专题详情（包含任务和待办事项）
    const topic = await topicService.getTopicById(topicId)
    console.log('加载的专题:', topic)
    
    if (topic) {
      // 加载任务列表
      const tasks = await taskService.getTasksByTopicId(topicId)
      console.log('加载的任务列表:', tasks)
      
      // 为每个任务加载待办事项
      for (const task of tasks) {
        console.log('为任务加载待办事项:', task.id)
        const todos = await todoService.getTodosByTaskId(task.id)
        console.log('任务待办事项:', todos)
        task.todos = todos
      }
      
      // 设置专题数据
      topic.tasks = tasks
      todoStore.selectedTopic = { ...topic }
      todoStore.selectedTask = null
      
      // 重要：同步更新 store 中的 topics 数组
      const topicIndex = todoStore.topics.findIndex(t => t.id === topicId)
      if (topicIndex > -1) {
        // 深度复制，确保所有嵌套数据都被正确更新
        todoStore.topics[topicIndex] = JSON.parse(JSON.stringify(topic))
        console.log('已同步更新 store 中的专题数据')
        console.log('更新后的专题数据:', todoStore.topics[topicIndex])
      } else {
        console.warn('未找到要更新的专题在 store 中的索引')
      }
      
      console.log('专题详情加载成功:', topic)
      console.log('设置后的 todoStore.selectedTopic:', todoStore.selectedTopic)
    } else {
      console.error('专题不存在:', topicId)
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('加载专题详情失败:', error)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

// 返回概览页
const goBack = () => {
  router.push('/dashboard')
}

// 切换任务详情显示
const toggleTaskDetail = async (task) => {
  console.log('=== 点击任务调试信息 ===')
  console.log('点击的任务:', task)
  console.log('任务ID:', task.id)
  console.log('当前 selectedTask:', selectedTask.value)
  console.log('todoStore.selectedTask:', todoStore.selectedTask)
  
  if (selectedTask.value?.id === task.id) {
    console.log('收起任务详情')
    todoStore.selectedTask = null
  } else {
    console.log('展开任务详情')
    
    // 如果任务还没有加载待办事项，先加载
    if (!task.todos || task.todos.length === 0) {
      try {
        console.log('开始加载任务待办事项:', task.id)
        const todos = await todoService.getTodosByTaskId(task.id)
        console.log('待办事项加载结果:', todos)
        task.todos = todos
      } catch (error) {
        console.error('加载待办事项失败:', error)
        task.todos = []
      }
    }
    
    // 创建新的任务对象
    const newSelectedTask = { ...task }
    console.log('准备设置的新任务:', newSelectedTask)
    
    // 更新 store 中的选中任务
    todoStore.selectedTask = newSelectedTask
    console.log('设置后的 todoStore.selectedTask:', todoStore.selectedTask)
    console.log('设置后的 selectedTask.value:', selectedTask.value)
  }
  
  console.log('=== 调试信息结束 ===')
}

// 编辑任务
const editTask = (task) => {
  selectedTaskForEdit.value = { ...task }
  showEditTask.value = true
}

// 删除任务（保留原有方法作为备用）
const deleteTask = async (taskId) => {
  if (!confirm('确定要删除这个任务吗？删除后无法恢复。')) return
  
  try {
    console.log('开始删除任务:', taskId)
    await taskService.deleteTask(taskId)
    console.log('后端删除成功')
    
    // 从 store 中移除
    if (selectedTopic.value) {
      const taskIndex = selectedTopic.value.tasks.findIndex(task => task.id === taskId)
      console.log('找到任务索引:', taskIndex)
      
      if (taskIndex > -1) {
        selectedTopic.value.tasks.splice(taskIndex, 1)
        console.log('从前端列表中移除任务')
        
        // 重新计算专题进度
        const topicProgress = getTopicProgress(selectedTopic.value)
        console.log('专题进度更新为:', topicProgress + '%')
        
        // 强制更新视图
        selectedTopic.value = { ...selectedTopic.value }
        console.log('专题视图已更新')
      } else {
        console.warn('未找到要删除的任务')
      }
    } else {
      console.warn('当前没有选中的专题')
    }
    
    // 如果删除的是当前选中的任务，清空选中状态
    if (selectedTask.value?.id === taskId) {
      todoStore.selectedTask = null
      console.log('清空选中的任务')
    }
    
  } catch (error) {
    console.error('删除任务失败:', error)
    alert('删除任务失败: ' + error.message)
  }
}

// 编辑待办事项
const editTodo = (todo) => {
  editingTodo.value = { ...todo }
}

// 删除待办事项
const deleteTodo = async (todoId) => {
  if (!confirm('确定要删除这个待办事项吗？')) return
  
  try {
    console.log('开始删除待办事项:', todoId)
    await todoService.deleteTodo(todoId)
    console.log('后端删除成功')
    
    // 从 store 中移除
    if (selectedTask.value) {
      const todoIndex = selectedTask.value.todos.findIndex(todo => todo.id === todoId)
      console.log('找到待办事项索引:', todoIndex)
      
      if (todoIndex > -1) {
        selectedTask.value.todos.splice(todoIndex, 1)
        console.log('从前端列表中移除待办事项')
        
        // 强制更新专题数据
        forceUpdateTopic();
      }
    } else {
      console.warn('当前没有选中的任务')
    }
  } catch (error) {
    console.error('删除待办事项失败:', error)
    alert('删除待办事项失败: ' + error.message)
  }
}

// 切换待办事项完成状态
const toggleTodoComplete = async (todo) => {
  try {
    const updatedTodo = { ...todo, isCompleted: !todo.isCompleted, completed: !todo.completed }
    await todoService.updateTodo(todo.id, updatedTodo)
    
    // 更新本地数据
    const todoIndex = selectedTask.value.todos.findIndex(t => t.id === todo.id)
    if (todoIndex > -1) {
      selectedTask.value.todos[todoIndex] = updatedTodo
    }
    
    // 强制更新专题数据
    forceUpdateTopic();
    
  } catch (error) {
    console.error('更新待办事项失败:', error)
    alert('更新待办事项失败: ' + error.message)
  }
}

// 更新待办事项进度（只保留这一个版本）
const updateTodoProgress = async (todo) => {
  try {
    // 如果进度是100%，自动标记为完成
    if (todo.progress >= 100) {
      todo.isCompleted = true;
      todo.completed = true;
    } else {
      todo.isCompleted = false;
      todo.completed = false;
    }
    
    // 调用后端 API 更新待办事项
    await todoService.updateTodo(todo.id, todo);
    
    // 强制更新专题数据
    forceUpdateTopic();
    
    console.log('待办事项进度已更新并同步')
  } catch (error) {
    console.error('更新待办事项进度失败:', error);
    alert('更新进度失败: ' + error.message);
  }
}

// 处理专题更新
const handleTopicUpdate = async (updatedTopic) => {
  try {
    // 调用后端 API 更新专题
    const result = await topicService.updateTopic(updatedTopic.id, updatedTopic)
    
    // 更新 store 中的数据
    const index = todoStore.topics.findIndex(t => t.id === updatedTopic.id)
    if (index > -1) {
      todoStore.topics[index] = result
    }
    
    if (selectedTopic.value && selectedTopic.value.id === updatedTopic.id) {
      todoStore.selectedTopic = { ...result }
    }
    
    showEditTopic.value = false
    alert('专题信息已更新！')
  } catch (error) {
    console.error('更新专题失败:', error)
    alert('更新专题失败: ' + error.message)
  }
}

// 处理专题删除
const handleTopicDelete = async (topicId) => {
  if (!confirm('确定要删除这个专题吗？删除后无法恢复。')) return
  
  try {
    await topicService.deleteTopic(topicId)
    
    // 从 store 中移除
    const index = todoStore.topics.findIndex(t => t.id === topicId)
    if (index > -1) {
      todoStore.topics.splice(index, 1)
    }
    
    // 如果删除的是当前选中的专题，返回概览页
    if (selectedTopic.value && selectedTopic.value.id === topicId) {
      todoStore.selectedTopic = null
      router.push('/dashboard')
    }
    
    showEditTopic.value = false
    alert('专题已删除！')
  } catch (error) {
    console.error('删除专题失败:', error)
    alert('删除专题失败: ' + error.message)
  }
}

// 处理任务创建
const handleTaskCreate = async (taskData) => {
  try {
    // 确保字段映射正确
    const taskPayload = {
      title: taskData.title,
      description: taskData.description || '',
      status: taskData.status || '待开始',
      priority: taskData.priority || '普通',
      dueDate: taskData.dueDate || null
    }
    
    // 调用后端 API 创建任务
    const newTask = await taskService.createTask(taskPayload, selectedTopic.value.id)
    
    // 更新 store 中的数据
    if (selectedTopic.value) {
      selectedTopic.value.tasks = selectedTopic.value.tasks || []
      selectedTopic.value.tasks.push(newTask)
    }
    
    // 同步到 store
    syncTopicToStore(selectedTopic.value)
    
    showCreateTask.value = false
    alert('任务创建成功！')
  } catch (error) {
    console.error('创建任务失败:', error)
    alert('创建任务失败: ' + error.message)
  }
}

// 处理任务更新
const handleTaskUpdate = async (updatedTask) => {
  try {
    // 调用后端 API 更新任务
    const result = await taskService.updateTask(updatedTask.id, updatedTask)
    
    // 更新 store 中的数据
    if (selectedTopic.value) {
      const taskIndex = selectedTopic.value.tasks.findIndex(task => task.id === updatedTask.id)
      if (taskIndex > -1) {
        selectedTopic.value.tasks[taskIndex] = result
      }
    }
    
    // 同步到 store
    syncTopicToStore(selectedTopic.value)
    
    if (selectedTask.value && selectedTask.value.id === updatedTask.id) {
      todoStore.selectedTask = { ...result }
    }
    
    showEditTask.value = false
    selectedTaskForEdit.value = null
    alert('任务更新成功！')
  } catch (error) {
    console.error('更新任务失败:', error)
    alert('更新任务失败: ' + error.message)
  }
}

// 处理任务删除
const handleTaskDelete = async (taskId) => {
  try {
    console.log('开始删除任务:', taskId)
    await taskService.deleteTask(taskId)
    console.log('后端删除成功')
    
    // 从 store 中移除
    if (selectedTopic.value) {
      const taskIndex = selectedTopic.value.tasks.findIndex(task => task.id === taskId)
      console.log('找到任务索引:', taskIndex)
      
      if (taskIndex > -1) {
        selectedTopic.value.tasks.splice(taskIndex, 1)
        console.log('从前端列表中移除任务')
        
        // 重新计算专题进度
        const topicProgress = getTopicProgress(selectedTopic.value)
        console.log('专题进度更新为:', topicProgress + '%')
        
        // 强制更新视图
        selectedTopic.value = { ...selectedTopic.value }
        console.log('专题视图已更新')
      } else {
        console.warn('未找到要删除的任务')
      }
    } else {
      console.warn('当前没有选中的专题')
    }
    
    // 如果删除的是当前选中的任务，清空选中状态
    if (selectedTask.value?.id === taskId) {
      todoStore.selectedTask = null
      console.log('清空选中的任务')
    }
    
    // 关闭编辑模态框
    showEditTask.value = false
    selectedTaskForEdit.value = null
    
  } catch (error) {
    console.error('删除任务失败:', error)
    alert('删除任务失败: ' + error.message)
  }
}

// 处理待办事项创建
const handleTodoCreate = async (todoData) => {
  try {
    // 确保字段映射正确
    const todoPayload = {
      title: todoData.title,
      content: todoData.content,
      priority: todoData.priority || '普通',
      isCompleted: Boolean(todoData.isCompleted),
      completed: Boolean(todoData.isCompleted),
      progress: parseInt(todoData.progress),
      note: todoData.note || ''
    }
    
    // 调用后端 API 创建待办事项
    const newTodo = await todoService.createTodo(todoPayload, selectedTask.value.id)
    
    // 更新 store 中的数据
    if (selectedTask.value) {
      selectedTask.value.todos = selectedTask.value.todos || []
      selectedTask.value.todos.push(newTodo)
    }
    
    // 同步到专题和 store
    syncTaskToTopic(selectedTask.value)
    
    showCreateTodo.value = false
    alert('待办事项创建成功！')
  } catch (error) {
    console.error('创建待办事项失败:', error)
    alert('创建待办事项失败: ' + error.message)
  }
}

// 处理待办事项更新
const handleTodoUpdate = async (updatedTodo) => {
  try {
    // 调用后端 API 更新待办事项
    const result = await todoService.updateTodo(updatedTodo.id, updatedTodo)
    
    // 更新 store 中的数据
    if (selectedTask.value) {
      const todoIndex = selectedTask.value.todos.findIndex(todo => todo.id === updatedTodo.id)
      if (todoIndex > -1) {
        selectedTask.value.todos[todoIndex] = result
      }
    }
    
    // 同步到专题和 store
    syncTaskToTopic(selectedTask.value)
    
    editingTodo.value = null
    alert('待办事项更新成功！')
  } catch (error) {
    console.error('更新待办事项失败:', error)
    alert('更新待办事项失败: ' + error.message)
  }
}

// 进度计算 - 从待办事项计算任务进度
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

// 进度计算 - 从任务计算专题进度
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

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #48bb78, #38a169)' // 绿色
  if (progress >= 50) return 'linear-gradient(90deg, #ed8936, #dd6b20)' // 橙色
  if (progress >= 20) return 'linear-gradient(90deg, #ecc94b, #d69e2e)' // 黄色
  return 'linear-gradient(90deg, #e53e3e, #c53030)' // 红色
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取已完成待办事项数量
const getCompletedTodos = (task) => {
  if (!task.todos) return 0
  return task.todos.filter(todo => todo.isCompleted || todo.completed).length
}

// 同步专题数据到 store
const syncTopicToStore = (updatedTopic) => {
  const topicIndex = todoStore.topics.findIndex(t => t.id === updatedTopic.id)
  if (topicIndex > -1) {
    todoStore.topics[topicIndex] = { ...updatedTopic }
    console.log('专题数据已同步到 store')
  }
}

// 同步任务数据到专题
const syncTaskToTopic = (updatedTask) => {
  if (selectedTopic.value) {
    const taskIndex = selectedTopic.value.tasks.findIndex(t => t.id === updatedTask.id)
    if (taskIndex > -1) {
      selectedTopic.value.tasks[taskIndex] = { ...updatedTask }
      // 同步到 store
      syncTopicToStore(selectedTopic.value)
    }
  }
}

// 同步待办事项数据到任务
const syncTodoToTask = (updatedTodo) => {
  if (selectedTask.value) {
    const todoIndex = selectedTask.value.todos.findIndex(t => t.id === updatedTodo.id)
    if (todoIndex > -1) {
      selectedTask.value.todos[todoIndex] = { ...updatedTodo }
      // 同步到专题
      syncTaskToTopic(selectedTask.value)
    }
  }
}

// 强制更新专题数据
const forceUpdateTopic = () => {
  if (selectedTopic.value) {
    // 触发响应式更新
    selectedTopic.value = { ...selectedTopic.value }
    
    // 同步到 store
    const topicIndex = todoStore.topics.findIndex(t => t.id === selectedTopic.value.id)
    if (topicIndex > -1) {
      todoStore.topics[topicIndex] = JSON.parse(JSON.stringify(selectedTopic.value))
    }
    
    console.log('强制更新专题数据完成')
  }
}

// 监听 selectedTopic 的变化，确保数据同步
watch(() => todoStore.selectedTopic, (newTopic) => {
  if (newTopic) {
    console.log('selectedTopic 发生变化:', newTopic)
    // 同步到 store 中的 topics 数组
    const topicIndex = todoStore.topics.findIndex(t => t.id === newTopic.id)
    if (topicIndex > -1) {
      todoStore.topics[topicIndex] = JSON.parse(JSON.stringify(newTopic))
      console.log('已同步 selectedTopic 到 store')
    }
  }
}, { deep: true })

// 监听 topics 的变化，确保左侧栏更新
watch(() => todoStore.topics, (newTopics) => {
  console.log('topics 发生变化:', newTopics)
}, { deep: true })
</script>

<style scoped>
/* 页面容器 */
.topic-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #4299e1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #718096;
  font-size: 16px;
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.error-state h3 {
  color: #e53e3e;
  margin-bottom: 16px;
  font-size: 24px;
}

.error-state p {
  color: #718096;
  margin-bottom: 24px;
  font-size: 16px;
}

/* 专题详情容器 */
.topic-detail {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  min-height: calc(100vh - 120px);
}

/* 专题头部样式 */
.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e9ecef;
}

.topic-header h2 {
  margin: 0;
  color: #2d3748;
  font-size: 28px;
  font-weight: 600;
  flex: 1;
  text-align: center;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
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
  transform: translateY(-1px);
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

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
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

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px dashed #dee2e6;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state h3 {
  color: #6c757d;
  margin-bottom: 8px;
  font-size: 20px;
}

.empty-state p {
  color: #adb5bd;
  margin: 0;
  font-size: 14px;
}

/* 任务列表样式 */
.task-list {
  display: grid;
  gap: 1rem;
  margin-bottom: 2rem;
}

.task-container {
  margin-bottom: 0;
}

/* 任务项样式 */
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
  background: white;
}

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

/* 待办清单样式 */
.todo-list {
  margin-bottom: 2rem;
}

.empty-todos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  background: white;
  border-radius: 8px;
  border: 2px dashed #dee2e6;
}

.empty-todos .empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.empty-todos h4 {
  color: #6c757d;
  margin-bottom: 8px;
  font-size: 16px;
}

.empty-todos p {
  color: #adb5bd;
  margin: 0;
  font-size: 14px;
}

.todo-item {
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 1rem;
  background: white;
  transition: all 0.2s ease;
}

.todo-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.todo-item.completed {
  background: #f0f9ff;
  border-color: #bfdbfe;
}

.todo-content {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.todo-checkbox {
  width: 18px;
  height: 18px;
  margin-top: 2px;
  cursor: pointer;
}

.todo-info {
  flex: 1;
}

.todo-text {
  font-size: 1rem;
  color: #2d3748;
  font-weight: 500;
  display: block;
  margin-bottom: 4px;
}

.todo-item.completed .todo-text {
  text-decoration: line-through;
  color: #718096;
}

.todo-description {
  font-size: 0.875rem;
  color: #718096;
  margin-bottom: 8px;
  line-height: 1.4;
}

.todo-meta {
  display: flex;
  gap: 12px;
  font-size: 0.75rem;
  color: #a0aec0;
}

.todo-priority {
  background: #edf2f7;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.todo-time {
  font-style: italic;
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
  -webkit-appearance: none;
  appearance: none;
}

.progress-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #4299e1;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #4299e1;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-value {
  min-width: 50px;
  text-align: center;
  font-size: 0.875rem;
  color: #718096;
  font-weight: 500;
}

.btn-edit {
  background: #4299e1;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.btn-edit:hover {
  background: #3182ce;
  transform: translateY(-1px);
}

/* 删除按钮样式 */
.btn-delete-small {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-delete-small:hover {
  background: #c53030;
}

.btn-delete-small:active {
  background: #9c2626;
}

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

/* 任务进度统计 */
.task-stats {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.task-stats h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 16px;
  font-weight: 600;
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
  font-size: 0.875rem;
}

.stat-item span:last-child {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.875rem;
}

/* 专题进度总览 */
.topic-progress-overview {
  margin-bottom: 2rem;
}

.progress-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.progress-card h4 {
  margin: 0 0 1rem 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.progress-bar-large {
  width: 100%;
  height: 12px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill-large {
  height: 100%;
  transition: width 0.5s ease;
  border-radius: 6px;
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-percentage {
  font-size: 1.5rem;
  font-weight: bold;
}

.progress-details {
  font-size: 0.875rem;
  opacity: 0.9;
}

/* 任务进度总览 */
.task-progress-overview {
  margin-bottom: 1.5rem;
}

.progress-card-small {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.progress-card-small h5 {
  margin: 0 0 0.5rem 0;
  color: #495057;
  font-size: 0.875rem;
  font-weight: 600;
}

.progress-bar-medium {
  width: 100%;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill-medium {
  height: 100%;
  transition: width 0.3s ease;
  border-radius: 4px;
}

.progress-stats-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  color: #6c757d;
}

.progress-stats-small span:first-child {
  font-weight: 600;
  color: #495057;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .topic-detail-page {
    padding: 10px;
  }
  
  .topic-detail {
    padding: 1rem;
  }
  
  .topic-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .topic-header h2 {
    font-size: 24px;
    text-align: left;
  }
  
  .header-actions {
    justify-content: space-between;
  }
  
  .btn-manage,
  .btn-back {
    flex: 1;
  }
  
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
  
  .stats-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
}
</style>