// src/utils/markdownParser.js

// 解析 Markdown 为 JSON 格式
export function parseMarkdownToJson(markdownText) {
  const lines = markdownText.split('\n')
  const topics = []
  let currentTopic = null
  let currentTask = null
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trim()
    
    // 解析专题 (## 开头)
    if (line.startsWith('## ')) {
      // 保存上一个专题
      if (currentTopic) {
        topics.push({ ...currentTopic })
      }
      
      // 开始新专题
      currentTopic = {
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9),
        name: line.substring(3).trim(),
        description: '',
        createdAt: new Date().toISOString(),
        tasks: []
      }
      currentTask = null
    }
    // 解析任务 (### 开头)
    else if (line.startsWith('### ') && currentTopic) {
      // 保存上一个任务
      if (currentTask) {
        currentTopic.tasks.push({ ...currentTask })
      }
      
      // 开始新任务
      currentTask = {
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9),
        name: line.substring(4).trim(),
        description: '',
        createdAt: new Date().toISOString(),
        todos: []
      }
    }
    // 解析待办事项 (- 开头)
    else if (line.startsWith('- ') && currentTask) {
      const todoText = line.substring(2).trim()
      const todo = {
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9),
        title: todoText,
        description: '',
        completed: false,
        progress: 0,
        note: '',
        createdAt: new Date().toISOString()
      }
      
      // 检查是否有完成标记
      if (todoText.startsWith('[x]') || todoText.startsWith('[X]')) {
        todo.completed = true
        todo.progress = 100
        todo.title = todoText.substring(3).trim()
      } else if (todoText.startsWith('[ ]')) {
        todo.title = todoText.substring(3).trim()
      }
      
      currentTask.todos.push(todo)
    }
    // 解析描述 (普通文本，非标题)
    else if (line && !line.startsWith('#') && !line.startsWith('-')) {
      if (currentTask) {
        currentTask.description += (currentTask.description ? '\n' : '') + line
      } else if (currentTopic) {
        currentTopic.description += (currentTopic.description ? '\n' : '') + line
      }
    }
  }
  
  // 保存最后一个专题和任务
  if (currentTask && currentTopic) {
    currentTopic.tasks.push({ ...currentTask })
  }
  if (currentTopic) {
    topics.push({ ...currentTopic })
  }
  
  return topics
}

// 转换 JSON 为 Markdown 格式
export function convertJsonToMarkdown(topics) {
  let markdown = '# TodoList 数据\n\n'
  
  topics.forEach(topic => {
    markdown += `## ${topic.name}\n\n`
    
    if (topic.description) {
      markdown += `${topic.description}\n\n`
    }
    
    if (topic.tasks && topic.tasks.length > 0) {
      topic.tasks.forEach(task => {
        markdown += `### ${task.name}\n\n`
        
        if (task.description) {
          markdown += `${task.description}\n\n`
        }
        
        if (task.todos && task.todos.length > 0) {
          task.todos.forEach(todo => {
            const checkbox = todo.completed ? '[x]' : '[ ]'
            markdown += `- ${checkbox} ${todo.title}\n`
            
            if (todo.note) {
              markdown += `  > 备注: ${todo.note}\n`
            }
          })
          markdown += '\n'
        }
      })
    }
  })
  
  return markdown
}

// 验证 Markdown 格式
export function validateMarkdownFormat(markdownText) {
  const errors = []
  const lines = markdownText.split('\n')
  
  let hasTopic = false
  let hasTask = false
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trim()
    
    if (line.startsWith('## ')) {
      hasTopic = true
    } else if (line.startsWith('### ')) {
      hasTask = true
    }
  }
  
  if (!hasTopic) {
    errors.push('未找到专题标题（## 开头的行）')
  }
  
  if (!hasTask) {
    errors.push('未找到任务标题（### 开头的行）')
  }
  
  return {
    valid: errors.length === 0,
    errors: errors
  }
}
