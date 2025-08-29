/**
 * Markdown解析器
 * 实现Markdown和TodoList JSON数据的互转
 */

// 将Markdown文本转换为JSON数据
export function parseMarkdownToJson(markdownText) {
  const lines = markdownText.split('\n').map(line => line.trim()).filter(line => line)
  const result = []
  let currentTopic = null
  let currentTask = null
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]
    
    // 解析专题（一级标题）
    if (line.startsWith('# ') && !line.startsWith('##')) {
      if (currentTopic) {
        result.push(currentTopic)
      }
      
      const topicName = line.substring(2).trim()
      currentTopic = {
        id: generateId(),
        name: topicName,
        description: '',
        createdAt: new Date().toISOString(),
        tasks: []
      }
      currentTask = null
      
      // 收集专题描述
      i = collectDescription(lines, i + 1, currentTopic)
    }
    
    // 解析任务（二级标题）
    else if (line.startsWith('## ')) {
      if (!currentTopic) {
        currentTopic = {
          id: generateId(),
          name: '默认专题',
          description: '',
          createdAt: new Date().toISOString(),
          tasks: []
        }
        result.push(currentTopic)
      }
      
      if (currentTask) {
        currentTopic.tasks.push(currentTask)
      }
      
      const taskName = line.substring(3).trim()
      currentTask = {
        id: generateId(),
        name: taskName,
        description: '',
        createdAt: new Date().toISOString(),
        todos: []
      }
      
      // 收集任务描述
      i = collectDescription(lines, i + 1, currentTask)
    }
    
    // 解析待办事项
    else if (line.startsWith('- ') && !line.startsWith('  - ')) {
      if (!currentTask) {
        if (!currentTopic) {
          currentTopic = {
            id: generateId(),
            name: '默认专题',
            description: '',
            createdAt: new Date().toISOString(),
            tasks: []
          }
          result.push(currentTopic)
        }
        
        currentTask = {
          id: generateId(),
          name: '默认任务',
          description: '',
          createdAt: new Date().toISOString(),
          todos: []
        }
        currentTopic.tasks.push(currentTask)
      }
      
      const todoTitle = line.substring(2).trim()
      const todo = {
        id: generateId(),
        title: todoTitle,
        description: '',
        completed: false,
        progress: 0,
        note: '',
        createdAt: new Date().toISOString()
      }
      
      // 解析待办的进度和备注
      i = parseTodoDetails(lines, i + 1, todo)
      
      currentTask.todos.push(todo)
    }
  }
  
  // 保存最后一个专题和任务
  if (currentTask) {
    currentTopic.tasks.push(currentTask)
  }
  if (currentTopic) {
    result.push(currentTopic)
  }
  
  return result
}

// 收集描述信息
function collectDescription(lines, startIndex, target) {
  let i = startIndex
  
  while (i < lines.length) {
    const line = lines[i]
    
    if (line.startsWith('#') || line.startsWith('-') || line.startsWith('###')) {
      break
    }
    
    if (line) {
      if (target.description) {
        target.description += '\n' + line
      } else {
        target.description = line
      }
    }
    
    i++
  }
  
  return i - 1
}

// 解析待办详情
function parseTodoDetails(lines, startIndex, todo) {
  let i = startIndex
  
  while (i < lines.length) {
    const line = lines[i]
    
    if (line.startsWith('- ') || line.startsWith('#') || line.startsWith('###')) {
      break
    }
    
    if (line.startsWith('  - 进度：')) {
      const progressMatch = line.match(/进度：(\d+)%/)
      if (progressMatch) {
        todo.progress = parseInt(progressMatch[1])
        if (todo.progress === 100) {
          todo.completed = true
        }
      }
    } else if (line.startsWith('  - 备注：')) {
      todo.note = line.substring(6).trim()
    }
    
    i++
  }
  
  return i - 1
}

// 将JSON数据转换为Markdown文本
export function convertJsonToMarkdown(topics) {
  let markdown = ''
  
  topics.forEach(topic => {
    markdown += `# ${topic.name}\n`
    
    if (topic.description) {
      markdown += `${topic.description}\n`
    }
    markdown += '\n'
    
    topic.tasks.forEach(task => {
      markdown += `## ${task.name}\n`
      
      if (task.description) {
        markdown += `${task.description}\n`
      }
      markdown += '\n'
      
      task.todos.forEach(todo => {
        markdown += `- ${todo.title}\n`
        
        if (todo.progress > 0) {
          markdown += `  - 进度：${todo.progress}%\n`
        }
        
        if (todo.note) {
          markdown += `  - 备注：${todo.note}\n`
        }
      })
      
      markdown += '\n'
    })
  })
  
  return markdown.trim()
}

// 验证Markdown格式
export function validateMarkdownFormat(markdownText) {
  const lines = markdownText.split('\n')
  let hasTopic = false
  let hasTask = false
  let hasTodo = false
  let errors = []
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]
    
    if (line.startsWith('# ') && !line.startsWith('##')) {
      hasTopic = true
    } else if (line.startsWith('## ')) {
      hasTask = true
      if (!hasTopic) {
        errors.push(`第${i+1}行：任务"${line}"不在任何专题内`)
      }
    } else if (line.startsWith('- ') && !line.startsWith('  - ')) {
      hasTodo = true
      if (!hasTask) {
        errors.push(`第${i+1}行：待办"${line}"不在任何任务内`)
      }
    }
  }
  
  if (!hasTopic) {
    errors.push('缺少专题标题（# 标题）')
  }
  
  if (!hasTask) {
    errors.push('缺少任务标题（## 标题）')
  }
  
  if (!hasTodo) {
    errors.push('缺少待办事项（- 内容）')
  }
  
  return {
    valid: errors.length === 0,
    errors: errors
  }
}

// 生成唯一ID
function generateId() {
  return Date.now().toString() + Math.random().toString(36).substr(2, 9)
} 