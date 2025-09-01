<!--
  进度概览组件 (ProgressOverview.vue)
  
  功能描述：
  - 提供三种视图模式：卡片视图、层级视图、图表视图
  - 显示总体进度统计和专题进度详情
  - 支持进度可视化展示（进度条、圆形进度、热力图等）
  - 提供交互式展开/折叠功能
  - 包含多种图表类型：雷达图、时间线、仪表盘、趋势分析等
  
  主要特性：
  - 响应式设计，支持移动端适配
  - 动态进度计算和颜色映射
  - 丰富的视觉反馈和动画效果
  - 模块化的图表组件设计
  
  数据依赖：
  - 接收 topics 数组作为 props
  - 自动计算各层级的进度百分比
  - 支持实时数据更新和重新渲染
-->
<template>
  <div class="progress-overview">
    <div class="overview-header">
      <h2>总体进度概览</h2>
      <div class="view-toggle">
        <button 
          @click="currentView = 'cards'" 
          :class="['toggle-btn', { active: currentView === 'cards' }]"
        >
          📊 卡片视图
        </button>
        <button 
          @click="currentView = 'hierarchy'" 
          :class="['toggle-btn', { active: currentView === 'hierarchy' }]"
        >
          📁 层级视图
        </button>
        <button 
          @click="currentView = 'charts'" 
          :class="['toggle-btn', { active: currentView === 'charts' }]"
        >
          📈 图表视图
        </button>
      </div>
    </div>

    <!-- 统计概览卡片 -->
    <div class="stats-overview">
      <div class="stat-card primary">
        <div class="stat-icon">📋</div>
        <div class="stat-content">
          <div class="stat-number">{{ topics.length }}</div>
          <div class="stat-label">总专题数</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">📝</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalTasks }}</div>
          <div class="stat-label">总任务数</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalTodos }}</div>
          <div class="stat-label">总待办数</div>
        </div>
      </div>
    </div>

    <!-- 总体进度条 -->
    <div class="overall-progress-card">
      <div class="progress-header">
        <div class="progress-title">
          <div class="progress-icon">🎯</div>
          <h3>总体进度</h3>
        </div>
        <div class="progress-percentage">{{ overallProgress }}%</div>
      </div>
      <div class="progress-track">
        <div 
          class="progress-bar-fill" 
          :style="{ 
            width: overallProgress + '%',
            background: getProgressGradient(overallProgress)
          }"
        >
          <div class="progress-shine"></div>
        </div>
      </div>
      <div class="progress-details">
        <div class="detail-item">
          <span class="detail-label">已完成专题</span>
          <span class="detail-value">{{ getCompletedTopics() }}/{{ topics.length }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">平均完成率</span>
          <span class="detail-value">{{ overallProgress }}%</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态</span>
          <span class="detail-value status" :class="getStatusClass(overallProgress)">
            {{ getStatusText(overallProgress) }}
          </span>
        </div>
      </div>
    </div>

    <!-- 卡片视图 -->
    <div v-if="currentView === 'cards'" class="cards-view">
      <div class="view-section">
        <h3>专题进度卡片</h3>
        <div class="cards-grid">
          <div 
            v-for="topic in topics" 
            :key="topic.id"
            class="topic-card"
            @click="toggleTopic(topic.id)"
          >
            <div class="card-header">
              <h4>{{ topic.name }}</h4>
              <div class="status-badge" :class="getStatusClass(getTopicProgress(topic))">
                {{ getStatusText(getTopicProgress(topic)) }}
              </div>
            </div>
            <div class="card-body">
              <div class="progress-circle">
                <svg width="80" height="80">
                  <circle 
                    cx="40" 
                    cy="40" 
                    r="35" 
                    stroke="#e9ecef" 
                    stroke-width="6" 
                    fill="none"
                  />
                  <circle 
                    cx="40" 
                    cy="40" 
                    r="35" 
                    :stroke="getProgressColor(getTopicProgress(topic))" 
                    stroke-width="6" 
                    fill="none"
                    stroke-dasharray="220"
                    :stroke-dashoffset="220 - (220 * getTopicProgress(topic) / 100)"
                    stroke-linecap="round"
                    transform="rotate(-90 40 40)"
                  />
                </svg>
                <div class="circle-text">{{ getTopicProgress(topic) }}%</div>
              </div>
              <div class="card-stats">
                <div class="stat-item">
                  <span class="stat-label">任务数</span>
                  <span class="stat-value">{{ topic.tasks.length }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">待办数</span>
                  <span class="stat-value">{{ getTopicTodosCount(topic) }}</span>
                </div>
              </div>
            </div>
            <div class="card-footer">
              <div class="mini-progress">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ 
                      width: getTopicProgress(topic) + '%',
                      background: getProgressColor(getTopicProgress(topic))
                    }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 层级视图 -->
    <div v-if="currentView === 'hierarchy'" class="hierarchy-view">
      <div class="hierarchy-menu">
        <div 
          v-for="topic in topics" 
          :key="topic.id" 
          class="menu-category"
        >
          <div class="category-header" @click="toggleTopic(topic.id)">
            <div class="category-info">
              <span class="arrow" :class="{ 'expanded': expandedTopics.has(topic.id) }">▶</span>
              <span class="category-name">{{ topic.name }}</span>
              <div class="progress-indicator">
                <div class="progress-dots">
                  <div 
                    v-for="i in 5" 
                    :key="i"
                    class="dot"
                    :class="{ 'active': getTopicProgress(topic) >= i * 20 }"
                  ></div>
                </div>
                <span class="progress-text">{{ getTopicProgress(topic) }}%</span>
              </div>
            </div>
          </div>
          
          <div class="sub-items" v-show="expandedTopics.has(topic.id)">
            <div 
              v-for="task in topic.tasks" 
              :key="task.id" 
              class="sub-category"
            >
              <div class="sub-category-header" @click="toggleTask(topic.id, task.id)">
                <div class="sub-category-info">
                  <span class="arrow" :class="{ 'expanded': expandedTasks.has(`${topic.id}-${task.id}`) }">▶</span>
                  <span class="sub-category-name">{{ task.name }}</span>
                  <div class="task-status">
                    <div class="status-dot" :style="{ backgroundColor: getProgressColor(getTaskProgress(task)) }"></div>
                    <span class="progress-text">{{ getTaskProgress(task) }}%</span>
                  </div>
                </div>
              </div>
              
              <div class="todo-items" v-show="expandedTasks.has(`${topic.id}-${task.id}`)">
                <div 
                  v-for="todo in task.todos" 
                  :key="todo.id" 
                  class="todo-item"
                >
                  <div class="todo-info">
                    <span class="todo-title">{{ todo.title }}</span>
                    <div class="todo-progress-indicator">
                      <div class="progress-steps">
                        <div 
                          v-for="step in 10" 
                          :key="step"
                          class="step"
                          :class="{ 'completed': (todo.progress || 0) >= step * 10 }"
                        ></div>
                      </div>
                      <span class="progress-text">{{ todo.progress || 0 }}%</span>
                    </div>
                  </div>
                  <div class="todo-note" v-if="todo.note">
                    <span class="note-label">📝</span>
                    <span class="note-content">{{ todo.note }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表视图 -->
    <div v-if="currentView === 'charts'" class="charts-view">
      <div class="charts-grid">
        <!-- 任务热力图 -->
        <div class="chart-card full-width">
          <h3>🔥 任务热力图</h3>
          <div class="heatmap-container">
            <div class="heatmap-legend">
              <div class="legend-title">进度图例</div>
              <div class="legend-items">
                <div class="legend-item">
                  <div class="legend-color heat-0"></div>
                  <span>0-19%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-1"></div>
                  <span>20-39%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-2"></div>
                  <span>40-59%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-3"></div>
                  <span>60-79%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-4"></div>
                  <span>80-99%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-5"></div>
                  <span>100%</span>
                </div>
              </div>
            </div>
            <div class="heatmap">
              <div 
                v-for="topic in topics" 
                :key="topic.id"
                class="heatmap-row"
              >
                <div class="heatmap-label">{{ topic.name }}</div>
                <div class="heatmap-cells">
                  <div 
                    v-for="task in topic.tasks" 
                    :key="task.id"
                    class="heatmap-cell"
                    :class="getHeatmapClass(getTaskProgress(task))"
                    :title="`${task.name}: ${getTaskProgress(task)}%`"
                  >
                    <div class="cell-value">{{ getTaskProgress(task) }}</div>
                  </div>
                  <div 
                    v-if="topic.tasks.length === 0"
                    class="heatmap-cell empty"
                    title="无任务"
                  >
                    -
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 专题待办热力图 -->
        <div class="chart-card full-width">
          <h3>📋 专题待办热力图</h3>
          <div class="heatmap-container">
            <div class="heatmap-legend">
              <div class="legend-title">待办进度图例</div>
              <div class="legend-items">
                <div class="legend-item">
                  <div class="legend-color heat-0"></div>
                  <span>0-19%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-1"></div>
                  <span>20-39%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-2"></div>
                  <span>40-59%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-3"></div>
                  <span>60-79%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-4"></div>
                  <span>80-99%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color heat-5"></div>
                  <span>100%</span>
                </div>
              </div>
            </div>
            <div class="todo-heatmap">
              <div 
                v-for="topic in topics" 
                :key="topic.id"
                class="todo-heatmap-section"
              >
                <div class="section-header">
                  <h4>{{ topic.name }}</h4>
                </div>
                <div class="todo-heatmap-grid">
                  <div 
                    v-for="task in topic.tasks" 
                    :key="task.id"
                    class="task-row"
                  >
                    <div class="task-name">{{ task.name }}</div>
                    <div class="todo-row">
                      <div 
                        v-for="todo in task.todos" 
                        :key="todo.id"
                        class="heatmap-cell todo-cell"
                        :class="getHeatmapClass(todo.progress || 0)"
                        :title="`${todo.title}: ${todo.progress || 0}%`"
                      >
                        <div class="cell-value">{{ todo.progress || 0 }}</div>
                      </div>
                      <div 
                        v-if="task.todos.length === 0"
                        class="heatmap-cell empty todo-cell"
                        title="无待办"
                      >
                        -
                      </div>
                    </div>
                  </div>
                  <div 
                    v-if="topic.tasks.length === 0"
                    class="no-tasks"
                  >
                    暂无任务
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  topics: {
    type: Array,
    default: () => []
  }
})

const currentView = ref('cards')
const expandedTopics = ref(new Set())
const expandedTasks = ref(new Set())

// 计算总任务数
const totalTasks = computed(() => {
  return props.topics.reduce((sum, topic) => sum + topic.tasks.length, 0)
})

// 计算总待办数
const totalTodos = computed(() => {
  return props.topics.reduce((sum, topic) => {
    return sum + topic.tasks.reduce((taskSum, task) => taskSum + task.todos.length, 0)
  }, 0)
})

// 计算总体进度
const overallProgress = computed(() => {
  if (props.topics.length === 0) return 0
  
  const totalProgress = props.topics.reduce((sum, topic) => {
    return sum + getTopicProgress(topic)
  }, 0)
  
  return Math.round(totalProgress / props.topics.length)
})

// 切换专题展开状态
const toggleTopic = (topicId) => {
  if (expandedTopics.value.has(topicId)) {
    expandedTopics.value.delete(topicId)
  } else {
    expandedTopics.value.add(topicId)
  }
}

// 切换任务展开状态
const toggleTask = (topicId, taskId) => {
  const key = `${topicId}-${taskId}`
  if (expandedTasks.value.has(key)) {
    expandedTasks.value.delete(key)
  } else {
    expandedTasks.value.add(key)
  }
}

// 获取专题进度
const getTopicProgress = (topic) => {
  if (topic.tasks.length === 0) return 0
  
  const totalProgress = topic.tasks.reduce((sum, task) => {
    return sum + getTaskProgress(task)
  }, 0)
  
  return Math.round(totalProgress / topic.tasks.length)
}

// 获取任务进度
const getTaskProgress = (task) => {
  if (task.todos.length === 0) return 0
  
  const totalProgress = task.todos.reduce((sum, todo) => {
    const progress = todo.progress || 0
    return sum + Math.min(progress, 100)
  }, 0)
  
  return Math.round(totalProgress / task.todos.length)
}

// 获取专题待办总数
const getTopicTodosCount = (topic) => {
  return topic.tasks.reduce((sum, task) => sum + task.todos.length, 0)
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return '#4CAF50'
  if (progress >= 60) return '#8BC34A'
  if (progress >= 40) return '#FFC107'
  if (progress >= 20) return '#FF9800'
  return '#F44336'
}

// 获取状态类名
const getStatusClass = (progress) => {
  if (progress === 100) return 'status-completed'
  if (progress === 0) return 'status-not-started'
  if (progress >= 80) return 'status-near-completion'
  if (progress >= 60) return 'status-progressing-well'
  if (progress >= 40) return 'status-in-progress'
  if (progress >= 20) return 'status-just-started'
  return 'status-pending'
}

// 获取状态文本
const getStatusText = (progress) => {
  if (progress === 100) return '已完成'
  if (progress === 0) return '未开始'
  if (progress >= 80) return '即将完成'
  if (progress >= 60) return '进展顺利'
  if (progress >= 40) return '正在进行'
  if (progress >= 20) return '刚刚开始'
  return '待启动'
}





// 获取进度渐变色
const getProgressGradient = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #4CAF50, #45a049)'
  if (progress >= 60) return 'linear-gradient(90deg, #8BC34A, #7CB342)'
  if (progress >= 40) return 'linear-gradient(90deg, #FFC107, #FFB300)'
  if (progress >= 20) return 'linear-gradient(90deg, #FF9800, #F57C00)'
  return 'linear-gradient(90deg, #F44336, #E53935)'
}

// 获取已完成专题数
const getCompletedTopics = () => {
  return props.topics.filter(topic => getTopicProgress(topic) >= 100).length
}

// 计算进度分布
const progressDistribution = computed(() => {
  const distribution = {
    completed: { label: '已完成', count: 0, color: '#28a745' },
    nearCompletion: { label: '即将完成', count: 0, color: '#20c997' },
    inProgress: { label: '进行中', count: 0, color: '#ffc107' },
    justStarted: { label: '刚开始', count: 0, color: '#fd7e14' },
    notStarted: { label: '未开始', count: 0, color: '#6c757d' }
  }
  
  props.topics.forEach(topic => {
    topic.tasks.forEach(task => {
      const progress = getTaskProgress(task)
      if (progress === 100) distribution.completed.count++
      else if (progress >= 80) distribution.nearCompletion.count++
      else if (progress >= 20) distribution.inProgress.count++
      else if (progress > 0) distribution.justStarted.count++
      else distribution.notStarted.count++
    })
  })
  
  return Object.values(distribution).filter(item => item.count > 0)
})

// 计算时间线数据
const timelineData = computed(() => {
  const data = []
  props.topics.forEach(topic => {
    topic.tasks.forEach(task => {
      data.push({
        title: `${topic.name} - ${task.name}`,
        progress: getTaskProgress(task),
        color: getProgressColor(getTaskProgress(task))
      })
    })
  })
  return data.sort((a, b) => b.progress - a.progress).slice(0, 8) // 只显示前8个
})

// 获取热力图类名
const getHeatmapClass = (progress) => {
  // 确保 progress 是数字类型
  const numProgress = Number(progress)
  
  if (numProgress === 100) return 'heat-5'
  if (numProgress >= 80) return 'heat-4'
  if (numProgress >= 60) return 'heat-3'
  if (numProgress >= 40) return 'heat-2'
  if (numProgress >= 20) return 'heat-1'
  return 'heat-0'
}

// 获取仪表盘弧度
const getGaugeArc = (progress) => {
  const angle = (progress / 100) * 160 // 160度范围
  const radian = (angle * Math.PI) / 180
  const x = 100 + 80 * Math.cos(Math.PI - radian)
  const y = 100 - 80 * Math.sin(Math.PI - radian)
  return `M 20 100 A 80 80 0 0 1 ${x} ${y}`
}

// 获取仪表盘指针位置
const getGaugePointerX = (progress) => {
  const angle = (progress / 100) * 160
  const radian = (angle * Math.PI) / 180
  return 100 + 70 * Math.cos(Math.PI - radian)
}

const getGaugePointerY = (progress) => {
  const angle = (progress / 100) * 160
  const radian = (angle * Math.PI) / 180
  return 100 - 70 * Math.sin(Math.PI - radian)
}

// 获取雷达图段样式
const getRadarSegmentStyle = (index, count, color) => {
  const total = progressDistribution.value.reduce((sum, item) => sum + item.count, 0)
  const percentage = total > 0 ? (count / total) * 100 : 0
  const baseAngle = index * 72 // 360/5 = 72度
  
  return {
    transform: `rotate(${baseAngle}deg)`,
    '--segment-size': `${Math.max(percentage, 10)}%`,
    '--segment-color': color
  }
}

// 获取各进度段的任务数量
const getHighProgressCount = () => {
  return props.topics.reduce((sum, topic) => {
    return sum + topic.tasks.filter(task => getTaskProgress(task) >= 80).length
  }, 0)
}

const getMediumProgressCount = () => {
  return props.topics.reduce((sum, topic) => {
    return sum + topic.tasks.filter(task => {
      const progress = getTaskProgress(task)
      return progress >= 40 && progress < 80
    }).length
  }, 0)
}

const getLowProgressCount = () => {
  return props.topics.reduce((sum, topic) => {
    return sum + topic.tasks.filter(task => getTaskProgress(task) < 40).length
  }, 0)
}

// 组件挂载时展开所有专题
onMounted(() => {
  props.topics.forEach(topic => {
    expandedTopics.value.add(topic.id)
  })
})
</script>

<style scoped>
.progress-overview {
  padding: 20px;
  background: #f8f9fa;
  min-height: auto;
}

.overview-header {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.overview-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.view-toggle {
  display: flex;
  gap: 10px;
}

.toggle-btn {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  color: #6c757d;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.toggle-btn:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

.toggle-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

/* 统计概览卡片 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.primary::before { background: #007bff; }
.stat-card.success::before { background: #28a745; }
.stat-card.warning::before { background: #ffc107; }
.stat-card.info::before { background: #17a2b8; }

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f8f9fa;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
  margin-top: 5px;
}

.progress-ring {
  position: relative;
  width: 60px;
  height: 60px;
}

.ring-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  font-weight: 600;
  color: #007bff;
}

/* 总体进度条卡片 */
.overall-progress-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  border-left: 4px solid #007bff;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.progress-title h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.progress-percentage {
  font-size: 28px;
  font-weight: 700;
  color: #007bff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.progress-track {
  width: 100%;
  height: 20px;
  background: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  margin-bottom: 20px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.8s ease;
  position: relative;
  overflow: hidden;
}

.progress-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.4), 
    transparent
  );
  animation: shine 2s infinite;
}

@keyframes shine {
  0% { left: -100%; }
  100% { left: 100%; }
}

.progress-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.detail-label {
  font-size: 12px;
  color: #6c757d;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.detail-value.status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.detail-value.status.status-completed { background: #28a745; }
.detail-value.status.status-not-started { background: #6c757d; }
.detail-value.status.status-near-completion { background: #20c997; }
.detail-value.status.status-progressing-well { background: #17a2b8; }
.detail-value.status.status-in-progress { background: #ffc107; color: #333; }
.detail-value.status.status-just-started { background: #fd7e14; }
.detail-value.status.status-pending { background: #dc3545; }

/* 卡片视图 */
.cards-view {
  margin-bottom: 30px;
}

.view-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.view-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.topic-card {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.topic-card:hover {
  border-color: #007bff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-header h4 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.status-completed { background: #28a745; }
.status-not-started { background: #6c757d; }
.status-near-completion { background: #20c997; }
.status-progressing-well { background: #17a2b8; }
.status-in-progress { background: #ffc107; color: #333; }
.status-just-started { background: #fd7e14; }
.status-pending { background: #dc3545; }

.card-body {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.progress-circle {
  position: relative;
  width: 80px;
  height: 80px;
}

.circle-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.card-stats {
  flex: 1;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 12px;
  color: #6c757d;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.card-footer {
  border-top: 1px solid #e9ecef;
  padding-top: 15px;
}

.mini-progress {
  width: 100%;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

/* 层级视图 */
.hierarchy-view {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.hierarchy-menu {
  padding: 0;
}

.menu-category {
  border-bottom: 1px solid #eee;
}

.menu-category:last-child {
  border-bottom: none;
}

.category-header {
  padding: 15px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-left: 4px solid #007bff;
}

.category-header:hover {
  background-color: #f8f9fa;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.arrow {
  font-size: 12px;
  transition: transform 0.2s;
  color: #666;
}

.arrow.expanded {
  transform: rotate(90deg);
}

.category-name {
  flex: 1;
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.progress-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-dots {
  display: flex;
  gap: 3px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e9ecef;
  transition: background-color 0.3s ease;
}

.dot.active {
  background: #007bff;
}

.progress-text {
  font-weight: 600;
  color: #007bff;
  min-width: 50px;
  text-align: right;
  font-size: 14px;
}

.sub-items {
  background-color: #f8f9fa;
  border-left: 2px solid #dee2e6;
  margin-left: 20px;
}

.sub-category {
  border-bottom: 1px solid #dee2e6;
}

.sub-category:last-child {
  border-bottom: none;
}

.sub-category-header {
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.sub-category-header:hover {
  background-color: #e9ecef;
}

.sub-category-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sub-category-name {
  flex: 1;
  font-weight: 500;
  color: #495057;
  font-size: 14px;
}

.task-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.todo-items {
  background-color: white;
  border-left: 2px solid #adb5bd;
  margin-left: 20px;
}

.todo-item {
  padding: 10px 20px;
  border-bottom: 1px solid #f1f3f4;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.todo-title {
  flex: 1;
  color: #495057;
  font-size: 13px;
}

.todo-progress-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-steps {
  display: flex;
  gap: 2px;
}

.step {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #e9ecef;
  transition: background-color 0.3s ease;
}

.step.completed {
  background: #28a745;
}

.todo-note {
  margin-top: 5px;
  font-size: 12px;
  color: #6c757d;
  display: flex;
  align-items: flex-start;
  gap: 5px;
}

.note-label {
  font-size: 14px;
}

.note-content {
  flex: 1;
  line-height: 1.4;
}

/* 图表视图 */
.charts-view {
  margin-bottom: 30px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

/* 图表卡片密集设计 */
.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 12px;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-card h3 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

/* 热力图 - 密集设计 */
.heatmap-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.heatmap-legend {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.legend-title {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  min-width: 60px;
}

.legend-items {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 10px;
  color: #6c757d;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.legend-color.heat-0 { 
  background: #e9ecef; 
  border-color: #adb5bd;
}
.legend-color.heat-1 { 
  background: #ffeaa7; 
  border-color: #fdcb6e;
}
.legend-color.heat-2 { 
  background: #fdcb6e; 
  border-color: #e17055;
}
.legend-color.heat-3 { 
  background: #fd79a8; 
  border-color: #e84393;
}
.legend-color.heat-4 { 
  background: #6c5ce7; 
  border-color: #5f3dc4;
}
.legend-color.heat-5 { 
  background: #00b894; 
  border-color: #00a085;
}

.heatmap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.heatmap-row {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.heatmap-label {
  min-width: 80px;
  font-size: 12px;
  font-weight: 500;
  color: #333;
  padding-top: 2px;
  line-height: 1.2;
}

.heatmap-cells {
  display: flex;
  gap: 3px;
  flex-wrap: wrap;
  flex: 1;
}

.task-column {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 60px;
}

.task-header {
  font-size: 12px;
  font-weight: 600;
  color: #495057;
  text-align: center;
  padding: 2px 4px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  word-break: break-word;
}

.todo-cells {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.heatmap-cell {
  width: 24px;
  height: 24px;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 8px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: transform 0.15s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.heatmap-cell:hover {
  transform: scale(1.2);
  z-index: 10;
  position: relative;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

.heatmap-cell.heat-0 { background: #e9ecef; color: #6c757d; }
.heatmap-cell.heat-1 { background: #ffeaa7; color: #333; }
.heatmap-cell.heat-2 { background: #fdcb6e; color: #333; }
.heatmap-cell.heat-3 { background: #fd79a8; color: white; }
.heatmap-cell.heat-4 { background: #6c5ce7; color: white; }
.heatmap-cell.heat-5 { background: #00b894; color: white; }

.heatmap-cell.empty {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px dashed #dee2e6;
}

.cell-value {
  font-size: 7px;
  font-weight: 700;
}

/* 待办热力图特殊样式 - 密集设计 */
.todo-heatmap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-heatmap-section {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 8px;
  border: 1px solid #e9ecef;
}

.section-header {
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #dee2e6;
}

.section-header h4 {
  margin: 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.todo-heatmap-grid {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.task-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  border-bottom: 1px solid #e9ecef;
}

.task-row:last-child {
  border-bottom: none;
}

.task-name {
  min-width: 80px;
  font-size: 11px;
  font-weight: 500;
  color: #495057;
  padding: 3px 6px;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  text-align: center;
  line-height: 1.2;
}

.todo-row {
  display: flex;
  gap: 3px;
  flex-wrap: wrap;
  flex: 1;
}

.no-tasks {
  text-align: center;
  color: #6c757d;
  font-style: italic;
  padding: 12px;
  background: white;
  border-radius: 4px;
  border: 1px dashed #dee2e6;
  font-size: 12px;
}

/* 更新热力图单元格样式 - 密集设计 */
.heatmap-cell.todo-cell {
  width: 20px;
  height: 20px;
  font-size: 7px;
  border-radius: 2px;
}

.heatmap-cell.todo-cell:hover {
  transform: scale(1.3);
  z-index: 10;
  position: relative;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .overview-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .view-toggle {
    justify-content: center;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .heatmap-legend {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  
  .legend-items {
    justify-content: flex-start;
  }
  
  .heatmap-row {
    flex-direction: column;
    gap: 3px;
  }
  
  .heatmap-label {
    min-width: auto;
    padding-top: 0;
    font-size: 11px;
  }
  
  .heatmap-cells {
    justify-content: flex-start;
  }
  
  .task-column {
    min-width: 50px;
  }
  
  .heatmap-cell {
    width: 20px;
    height: 20px;
    font-size: 7px;
  }
  
  .heatmap-cell.todo-cell {
    width: 18px;
    height: 18px;
    font-size: 6px;
  }
  
  .task-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .task-name {
    min-width: auto;
    width: 100%;
    text-align: left;
    font-size: 10px;
  }
  
  .todo-row {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 480px) {
  .todo-heatmap-section {
    padding: 6px;
  }
  
  .section-header h4 {
    font-size: 12px;
  }
  
  .task-name {
    font-size: 9px;
    padding: 2px 4px;
  }
  
  .heatmap-cell.todo-cell {
    width: 16px;
    height: 16px;
    font-size: 6px;
  }
  
  .todo-row {
    gap: 2px;
  }
  
  .heatmap-cell {
    width: 18px;
    height: 18px;
    font-size: 6px;
  }
  
  .heatmap-label {
    font-size: 10px;
  }
}
</style> 