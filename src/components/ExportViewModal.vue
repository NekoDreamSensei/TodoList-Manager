<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content export-modal" @click.stop>
      <div class="modal-header">
        <h3>📊 导出视图</h3>
        <button @click="closeModal" class="btn-close">×</button>
      </div>
      
      <div class="modal-body">
        <!-- 导出格式选择 -->
        <div class="export-section">
          <h4>📄 导出格式</h4>
          <div class="format-options">
            <label class="format-option" :class="{ active: selectedFormat === 'pdf' }">
              <input type="radio" v-model="selectedFormat" value="pdf" />
              <div class="format-icon">📄</div>
              <div class="format-info">
                <div class="format-name">PDF报告</div>
                <div class="format-desc">适合打印和分享</div>
              </div>
            </label>
            
            <label class="format-option" :class="{ active: selectedFormat === 'excel' }">
              <input type="radio" v-model="selectedFormat" value="excel" />
              <div class="format-icon">📊</div>
              <div class="format-info">
                <div class="format-name">Excel表格</div>
                <div class="format-desc">适合数据分析</div>
              </div>
            </label>
            
            <label class="format-option" :class="{ active: selectedFormat === 'csv' }">
              <input type="radio" v-model="selectedFormat" value="csv" />
              <div class="format-icon">📋</div>
              <div class="format-info">
                <div class="format-name">CSV文件</div>
                <div class="format-desc">适合导入其他系统</div>
              </div>
            </label>
            
            <label class="format-option" :class="{ active: selectedFormat === 'markdown' }">
              <input type="radio" v-model="selectedFormat" value="markdown" />
              <div class="format-icon">📝</div>
              <div class="format-info">
                <div class="format-name">Markdown</div>
                <div class="format-desc">适合文档编辑</div>
              </div>
            </label>
          </div>
        </div>

        <!-- 视图类型选择 -->
        <div class="export-section">
          <h4>👁️ 视图类型</h4>
          <div class="view-options">
            <label class="view-option" :class="{ active: selectedView === 'overview' }">
              <input type="radio" v-model="selectedView" value="overview" />
              <div class="view-icon">📊</div>
              <div class="view-info">
                <div class="view-name">进度概览</div>
                <div class="view-desc">总体进度和统计信息</div>
              </div>
            </label>
            
            <label class="view-option" :class="{ active: selectedView === 'detailed' }">
              <input type="radio" v-model="selectedView" value="detailed" />
              <div class="view-icon">📋</div>
              <div class="view-info">
                <div class="view-name">详细列表</div>
                <div class="view-desc">完整的任务和待办列表</div>
              </div>
            </label>
            
            <label class="view-option" :class="{ active: selectedView === 'completed' }">
              <input type="radio" v-model="selectedView" value="completed" />
              <div class="view-icon">✅</div>
              <div class="view-info">
                <div class="view-name">已完成</div>
                <div class="view-desc">只显示已完成的任务</div>
              </div>
            </label>
            
            <label class="view-option" :class="{ active: selectedView === 'pending' }">
              <input type="radio" v-model="selectedView" value="pending" />
              <div class="view-icon">⏳</div>
              <div class="view-info">
                <div class="view-name">待完成</div>
                <div class="view-desc">只显示未完成的任务</div>
              </div>
            </label>
          </div>
        </div>

        <!-- 筛选选项 -->
        <div class="export-section">
          <h4>🔍 筛选选项</h4>
          <div class="filter-options">
            <div class="filter-group">
              <label>专题筛选：</label>
              <select v-model="selectedTopics" multiple class="filter-select">
                <option value="all">全部专题</option>
                <option v-for="topic in topics" :key="topic.id" :value="topic.id">
                  {{ topic.name }}
                </option>
              </select>
            </div>
            
            <div class="filter-group">
              <label>进度范围：</label>
              <div class="progress-range">
                <input type="range" v-model="minProgress" min="0" max="100" step="5" />
                <span>{{ minProgress }}% - 100%</span>
              </div>
            </div>
            
            <div class="filter-group">
              <label>
                <input type="checkbox" v-model="includeNotes" />
                包含备注信息
              </label>
            </div>
            
            <div class="filter-group">
              <label>
                <input type="checkbox" v-model="includeDates" />
                包含创建日期
              </label>
            </div>
          </div>
        </div>

        <!-- 预览区域 -->
        <div class="export-section">
          <h4>👀 预览</h4>
          <div class="preview-area">
            <div class="preview-content" v-html="previewContent"></div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button @click="closeModal" class="btn-cancel">取消</button>
        <button @click="exportData" class="btn-export" :disabled="!selectedFormat">
          📥 导出 {{ getFormatDisplayName(selectedFormat) }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  topics: {
    type: Array,
    required: true
  },
  username: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['close'])

// 导出选项
const selectedFormat = ref('pdf')
const selectedView = ref('overview')
const selectedTopics = ref(['all'])
const minProgress = ref(0)
const includeNotes = ref(true)
const includeDates = ref(true)

// 预览内容
const previewContent = computed(() => {
  const filteredData = getFilteredData()
  return generatePreview(filteredData)
})

// 获取筛选后的数据
const getFilteredData = () => {
  let filteredTopics = props.topics
  
  // 按专题筛选
  if (!selectedTopics.value.includes('all')) {
    filteredTopics = props.topics.filter(topic => 
      selectedTopics.value.includes(topic.id)
    )
  }
  
  // 按视图类型筛选
  switch (selectedView.value) {
    case 'completed':
      filteredTopics = filteredTopics.map(topic => ({
        ...topic,
        tasks: topic.tasks.map(task => ({
          ...task,
          todos: task.todos.filter(todo => todo.completed)
        })).filter(task => task.todos.length > 0)
      })).filter(topic => topic.tasks.length > 0)
      break
      
    case 'pending':
      filteredTopics = filteredTopics.map(topic => ({
        ...topic,
        tasks: topic.tasks.map(task => ({
          ...task,
          todos: task.todos.filter(todo => !todo.completed)
        })).filter(task => task.todos.length > 0)
      })).filter(topic => topic.tasks.length > 0)
      break
  }
  
  return filteredTopics
}

// 生成预览内容
const generatePreview = (data) => {
  if (selectedView.value === 'overview') {
    return generateOverviewPreview(data)
  } else {
    return generateDetailedPreview(data)
  }
}

// 生成概览预览
const generateOverviewPreview = (data) => {
  const totalTopics = data.length
  const totalTasks = data.reduce((sum, topic) => sum + topic.tasks.length, 0)
  const totalTodos = data.reduce((sum, topic) => 
    sum + topic.tasks.reduce((taskSum, task) => taskSum + task.todos.length, 0), 0
  )
  const completedTodos = data.reduce((sum, topic) => 
    sum + topic.tasks.reduce((taskSum, task) => 
      taskSum + task.todos.filter(todo => todo.completed).length, 0), 0
  )
  
  const completionRate = totalTodos > 0 ? Math.round((completedTodos / totalTodos) * 100) : 0
  
  return `
    <div class="preview-overview">
      <h3>📊 进度概览报告</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-number">${totalTopics}</div>
          <div class="stat-label">专题数量</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">${totalTasks}</div>
          <div class="stat-label">任务数量</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">${totalTodos}</div>
          <div class="stat-label">待办总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">${completionRate}%</div>
          <div class="stat-label">完成率</div>
        </div>
      </div>
    </div>
  `
}

// 生成详细预览
const generateDetailedPreview = (data) => {
  let html = '<div class="preview-detailed"><h3>📋 详细任务列表</h3>'
  
  data.forEach(topic => {
    html += `<div class="topic-section">
      <h4>📁 ${topic.name}</h4>
      <p class="topic-desc">${topic.description || '无描述'}</p>`
    
    topic.tasks.forEach(task => {
      html += `<div class="task-section">
        <h5>�� ${task.name}</h5>
        <p class="task-desc">${task.description || '无描述'}</p>
        <ul class="todo-list">`
      
      task.todos.forEach(todo => {
        const status = todo.completed ? '✅' : '⏳'
        const progress = todo.progress || 0
        html += `<li class="todo-item">
          <span class="todo-status">${status}</span>
          <span class="todo-title">${todo.title}</span>
          <span class="todo-progress">${progress}%</span>
          ${includeNotes.value && todo.note ? `<div class="todo-note">💬 ${todo.note}</div>` : ''}
          ${includeDates.value ? `<div class="todo-date">📅 ${new Date(todo.createdAt).toLocaleDateString()}</div>` : ''}
        </li>`
      })
      
      html += '</ul></div>'
    })
    
    html += '</div>'
  })
  
  html += '</div>'
  return html
}

// 获取格式显示名称
const getFormatDisplayName = (format) => {
  const names = {
    pdf: 'PDF',
    excel: 'Excel',
    csv: 'CSV',
    markdown: 'Markdown'
  }
  return names[format] || format
}

// 导出数据
const exportData = () => {
  const filteredData = getFilteredData()
  
  switch (selectedFormat.value) {
    case 'pdf':
      exportAsPDF(filteredData)
      break
    case 'excel':
      exportAsExcel(filteredData)
      break
    case 'csv':
      exportAsCSV(filteredData)
      break
    case 'markdown':
      exportAsMarkdown(filteredData)
      break
  }
  
  closeModal()
}

// 导出为PDF
const exportAsPDF = (data) => {
  // 这里可以使用jsPDF库来生成PDF
  // 暂时使用简单的HTML转PDF方法
  const content = generatePreview(data)
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html>
      <head>
        <title>TodoList导出报告</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          .preview-overview, .preview-detailed { margin: 20px 0; }
          .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin: 20px 0; }
          .stat-item { text-align: center; padding: 15px; border: 1px solid #ddd; border-radius: 8px; }
          .stat-number { font-size: 24px; font-weight: bold; color: #667eea; }
          .stat-label { color: #666; margin-top: 5px; }
          .topic-section, .task-section { margin: 20px 0; }
          .todo-list { list-style: none; padding: 0; }
          .todo-item { padding: 10px; border-bottom: 1px solid #eee; }
        </style>
      </head>
      <body>
        <h1>�� TodoList导出报告</h1>
        <p>导出时间：${new Date().toLocaleString()}</p>
        <p>用户：${props.username}</p>
        ${content}
      </body>
    </html>
  `)
  printWindow.document.close()
  printWindow.print()
}

// 导出为Excel
const exportAsExcel = (data) => {
  // 生成CSV格式的Excel文件
  let csvContent = '专题,任务,待办,状态,进度,备注,创建日期\n'
  
  data.forEach(topic => {
    topic.tasks.forEach(task => {
      task.todos.forEach(todo => {
        const status = todo.completed ? '已完成' : '未完成'
        const progress = todo.progress || 0
        const note = todo.note || ''
        const date = includeDates.value ? new Date(todo.createdAt).toLocaleDateString() : ''
        
        csvContent += `"${topic.name}","${task.name}","${todo.title}","${status}","${progress}%","${note}","${date}"\n`
      })
    })
  })
  
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${props.username}_todolist_export.csv`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

// 导出为CSV
const exportAsCSV = (data) => {
  exportAsExcel(data) // 使用相同的逻辑
}

// 导出为Markdown
const exportAsMarkdown = (data) => {
  let markdown = `# �� TodoList导出报告\n\n`
  markdown += `**导出时间：** ${new Date().toLocaleString()}\n`
  markdown += `**用户：** ${props.username}\n\n`
  
  if (selectedView.value === 'overview') {
    const totalTopics = data.length
    const totalTasks = data.reduce((sum, topic) => sum + topic.tasks.length, 0)
    const totalTodos = data.reduce((sum, topic) => 
      sum + topic.tasks.reduce((taskSum, task) => taskSum + task.todos.length, 0), 0
    )
    const completedTodos = data.reduce((sum, topic) => 
      sum + topic.tasks.reduce((taskSum, task) => 
        taskSum + task.todos.filter(todo => todo.completed).length, 0), 0
    )
    const completionRate = totalTodos > 0 ? Math.round((completedTodos / totalTodos) * 100) : 0
    
    markdown += `## �� 统计概览\n\n`
    markdown += `- **专题数量：** ${totalTopics}\n`
    markdown += `- **任务数量：** ${totalTasks}\n`
    markdown += `- **待办总数：** ${totalTodos}\n`
    markdown += `- **完成率：** ${completionRate}%\n\n`
  }
  
  data.forEach(topic => {
    markdown += `## 📁 ${topic.name}\n\n`
    if (topic.description) {
      markdown += `${topic.description}\n\n`
    }
    
    topic.tasks.forEach(task => {
      markdown += `### �� ${task.name}\n\n`
      if (task.description) {
        markdown += `${task.description}\n\n`
      }
      
      task.todos.forEach(todo => {
        const status = todo.completed ? '✅' : '⏳'
        const progress = todo.progress || 0
        markdown += `- ${status} **${todo.title}** (${progress}%)\n`
        
        if (includeNotes.value && todo.note) {
          markdown += `  - 💬 ${todo.note}\n`
        }
        
        if (includeDates.value) {
          markdown += `  - 📅 ${new Date(todo.createdAt).toLocaleDateString()}\n`
        }
      })
      
      markdown += '\n'
    })
  })
  
  const blob = new Blob([markdown], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${props.username}_todolist_export.md`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

const closeModal = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.export-modal {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.5rem 0;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 1rem;
}

.modal-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-close:hover {
  background: #f7fafc;
  color: #4a5568;
}

.modal-body {
  padding: 1.5rem;
}

.export-section {
  margin-bottom: 2rem;
}

.export-section h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
}

.format-options, .view-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.format-option, .view-option {
  display: flex;
  align-items: center;
  padding: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.format-option:hover, .view-option:hover {
  border-color: #cbd5e0;
  background: #f7fafc;
}

.format-option.active, .view-option.active {
  border-color: #667eea;
  background: #ebf8ff;
}

.format-option input, .view-option input {
  margin-right: 0.75rem;
}

.format-icon, .view-icon {
  font-size: 1.5rem;
  margin-right: 0.75rem;
}

.format-info, .view-info {
  flex: 1;
}

.format-name, .view-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.format-desc, .view-desc {
  font-size: 0.875rem;
  color: #718096;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  min-width: 100px;
  font-weight: 500;
  color: #2d3748;
}

.filter-select {
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  min-width: 200px;
}

.progress-range {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.progress-range input {
  flex: 1;
}

.preview-area {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  max-height: 300px;
  overflow-y: auto;
  background: #f8f9fa;
}

.preview-content {
  font-size: 0.875rem;
  line-height: 1.5;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  background: #718096;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #4a5568;
}

.btn-export {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-export:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-export:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 预览样式 */
.preview-overview .stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin: 1rem 0;
}

.preview-overview .stat-item {
  text-align: center;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
}

.preview-overview .stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #667eea;
}

.preview-overview .stat-label {
  color: #718096;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.preview-detailed .topic-section,
.preview-detailed .task-section {
  margin: 1rem 0;
}

.preview-detailed .todo-list {
  list-style: none;
  padding: 0;
  margin: 0.5rem 0;
}

.preview-detailed .todo-item {
  padding: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.preview-detailed .todo-status {
  font-size: 1rem;
}

.preview-detailed .todo-title {
  flex: 1;
  font-weight: 500;
}

.preview-detailed .todo-progress {
  color: #667eea;
  font-weight: 500;
}

.preview-detailed .todo-note,
.preview-detailed .todo-date {
  font-size: 0.75rem;
  color: #718096;
  margin-top: 0.25rem;
}
</style> 