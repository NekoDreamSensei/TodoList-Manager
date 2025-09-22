<template>
  <div class="dashboard-page">
    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div class="user-avatar">
        {{ currentUser?.username?.charAt(0).toUpperCase() || 'U' }}
      </div>
      <div class="user-details">
        <h3>{{ currentUser?.username || '未登录' }}</h3>
        <p>欢迎使用 TodoList 管理系统</p>
        <p>创建时间: {{ new Date().toLocaleDateString() }}</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>总专题数</h3>
        <div class="stat-number">{{ topics.length }}</div>
      </div>
      <div class="stat-card">
        <h3>总任务数</h3>
        <div class="stat-number">{{ totalTasks }}</div>
      </div>
      <div class="stat-card">
        <h3>总待办数</h3>
        <div class="stat-number">{{ totalTodos }}</div>
      </div>
      <div class="stat-card">
        <h3>完成率</h3>
        <div class="stat-number">{{ overallProgress }}%</div>
      </div>
    </div>

    <!-- 专题概览 -->
    <div class="topics-overview">
      <h2>专题概览</h2>
      <div v-if="topics.length === 0" class="empty-state">
        <p>还没有创建任何专题</p>
        <p>点击左侧的"新建专题"按钮开始创建您的第一个专题吧！</p>
      </div>
      <div v-else class="topics-grid">
        <div 
          v-for="topic in topics" 
          :key="topic.id"
          class="topic-card"
          @click="selectTopic(topic)"
        >
          <div class="topic-header">
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
          <div class="topic-stats">
            <div class="stat-item">
              <span>任务数:</span>
              <span>{{ topic.tasks?.length || 0 }}</span>
            </div>
            <div class="stat-item">
              <span>待办数:</span>
              <span>{{ getTopicTodosCount(topic) }}</span>
            </div>
          </div>
          <div v-if="topic.description" class="topic-description">
            {{ topic.description.length > 100 ? topic.description.substring(0, 100) + '...' : topic.description }}
          </div>
        </div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activity">
      <h2>最近活动</h2>
      <div v-if="recentActivities.length === 0" class="empty-state">
        <p>暂无最近活动</p>
      </div>
      <div v-else class="activity-list">
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon">{{ activity.icon }}</div>
          <div class="activity-content">
            <p>{{ activity.description }}</p>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../services/authService.js'
import { useTodoStore } from '../stores/useTodoStore.js'

const router = useRouter()
const todoStore = useTodoStore()

// 从 store 获取数据
const topics = computed(() => todoStore.topics)
const currentUser = computed(() => authService.getCurrentUser())

// 统计数据
const totalTasks = computed(() => {
  return topics.value.reduce((sum, topic) => sum + (topic.tasks?.length || 0), 0)
})

const totalTodos = computed(() => {
  return topics.value.reduce((sum, topic) => {
    return sum + (topic.tasks?.reduce((taskSum, task) => taskSum + (task.todos?.length || 0), 0) || 0)
  }, 0)
})

const overallProgress = computed(() => {
  if (topics.value.length === 0) return 0
  
  const totalProgress = topics.value.reduce((sum, topic) => {
    return sum + getTopicProgress(topic)
  }, 0)
  
  return Math.round(totalProgress / topics.value.length)
})

// 最近活动
const recentActivities = ref([])

// 初始化
onMounted(async () => {
  console.log('🎯 DashboardPage 页面加载')
  
  // 检查认证状态
  const isAuthenticated = authService.isAuthenticated()
  
  if (isAuthenticated) {
    // 加载用户数据
    todoStore.loadUserTopics(currentUser.value.username)
    // 生成最近活动
    generateRecentActivities()
  } else {
    router.push('/login')
  }
})

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
  
  task.todos.forEach(todo => {
    if (todo.completed) {
      totalProgress += 100
    } else {
      totalProgress += Math.min(todo.progress || 0, 100)
    }
  })
  
  const averageProgress = Math.round(totalProgress / totalCount)
  return Math.min(averageProgress, 100)
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #48bb78, #38a169)'
  if (progress >= 50) return 'linear-gradient(90deg, #ed8936, #dd6b20)'
  if (progress >= 20) return 'linear-gradient(90deg, #ecc94b, #d69e2e)'
  return 'linear-gradient(90deg, #e53e3e, #c53030)'
}

// 获取专题待办数量
const getTopicTodosCount = (topic) => {
  return topic.tasks?.reduce((sum, task) => sum + (task.todos?.length || 0), 0) || 0
}

// 选择专题
const selectTopic = (topic) => {
  todoStore.selectedTopic = { ...topic }
  todoStore.selectedTask = null
  router.push(`/topic/${topic.id}`)
}

// 生成最近活动
const generateRecentActivities = () => {
  const activities = []
  
  // 模拟最近活动数据
  if (topics.value.length > 0) {
    activities.push({
      id: 1,
      icon: '📝',
      description: `创建了专题 "${topics.value[0].name}"`,
      time: '刚刚'
    })
  }
  
  if (totalTasks.value > 0) {
    activities.push({
      id: 2,
      icon: '✅',
      description: `完成了 ${Math.floor(totalTodos.value * 0.3)} 个待办事项`,
      time: '1小时前'
    })
  }
  
  activities.push({
    id: 3,
    icon: '🎯',
    description: '登录系统',
    time: '今天'
  })
  
  recentActivities.value = activities
}
</script>

<style scoped>
.dashboard-page {
  padding: 0;
  margin: -2rem;
  background: #f8f9fa;
  border-radius: 12px;
  min-height: calc(100vh - 120px);
  overflow-y: auto;
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

.topics-overview {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.topics-overview h2 {
  margin: 0 0 2rem 0;
  color: #2d3748;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #718096;
}

.empty-state p {
  margin: 0.5rem 0;
}

.topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.topic-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.topic-card:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.topic-header {
  margin-bottom: 1rem;
}

.topic-header h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.1rem;
}

.topic-progress {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.875rem;
  color: #718096;
  font-weight: 600;
}

.topic-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.875rem;
}

.stat-item span:first-child {
  color: #718096;
}

.stat-item span:last-child {
  font-weight: 600;
  color: #2d3748;
}

.topic-description {
  font-size: 0.875rem;
  color: #718096;
  line-height: 1.4;
}

.recent-activity {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.recent-activity h2 {
  margin: 0 0 2rem 0;
  color: #2d3748;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.activity-icon {
  font-size: 1.5rem;
}

.activity-content p {
  margin: 0 0 0.25rem 0;
  color: #2d3748;
}

.activity-time {
  font-size: 0.875rem;
  color: #718096;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .topics-grid {
    grid-template-columns: 1fr;
  }
  
  .user-info-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>