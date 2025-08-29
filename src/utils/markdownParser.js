/**
 * Markdown解析器
 * 实现Markdown和TodoList JSON数据的互转
 */

// 将Markdown文本转换为JSON数据
export function parseMarkdownToJson(markdownText) {
  const lines = markdownText.split('\n')
  const result = []
  let currentTopic = null
  let currentTask = null
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]
    const trimmedLine = line.trim()
    
    // 跳过空行
    if (!trimmedLine) continue
    
    // 解析专题（一级标题）
    if (trimmedLine.startsWith('# ') && !trimmedLine.startsWith('##')) {
      if (currentTopic) {
        result.push(currentTopic)
      }
      
      const topicName = trimmedLine.substring(2).trim()
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
    else if (trimmedLine.startsWith('## ')) {
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
      
      const taskName = trimmedLine.substring(3).trim()
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
    
    // 解析待办事项（一级无序列表）
    else if (trimmedLine.startsWith('- ') && getIndentLevel(line) === 0) {
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
      
      const todoTitle = trimmedLine.substring(2).trim()
      const todo = {
        id: generateId(),
        title: todoTitle,
        description: '',
        completed: false,
        progress: 0,
        note: '',
        createdAt: new Date().toISOString()
      }
      
      // 解析待办的进度和备注（二级无序列表）
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

// 获取行的缩进级别（以2个空格为一个缩进单位）
function getIndentLevel(line) {
  const leadingSpaces = line.length - line.trimStart().length
  return Math.floor(leadingSpaces / 2)
}

// 收集描述信息
function collectDescription(lines, startIndex, target) {
  let i = startIndex
  
  while (i < lines.length) {
    const line = lines[i]
    const trimmedLine = line.trim()
    
    // 跳过空行
    if (!trimmedLine) {
      i++
      continue
    }
    
    // 遇到新的标题或一级无序列表时停止
    if (trimmedLine.startsWith('#') || (trimmedLine.startsWith('- ') && getIndentLevel(line) === 0)) {
      break
    }
    
    // 添加到描述中
    if (target.description) {
      target.description += '\n' + trimmedLine
    } else {
      target.description = trimmedLine
    }
    
    i++
  }
  
  return i - 1
}

// 解析待办详情（只处理二级无序列表）
function parseTodoDetails(lines, startIndex, todo) {
  let i = startIndex
  
  while (i < lines.length) {
    const line = lines[i]
    const trimmedLine = line.trim()
    
    // 跳过空行
    if (!trimmedLine) {
      i++
      continue
    }
    
    // 遇到新的标题或一级无序列表时停止
    if (trimmedLine.startsWith('#') || (trimmedLine.startsWith('- ') && getIndentLevel(line) === 0)) {
      break
    }
    
    // 只处理二级无序列表（缩进级别为1）
    if (trimmedLine.startsWith('- ') && getIndentLevel(line) === 1) {
      if (trimmedLine.startsWith('- 进度：')) {
        const progressMatch = trimmedLine.match(/进度：(\d+)%/)
        if (progressMatch) {
          todo.progress = parseInt(progressMatch[1])
          if (todo.progress === 100) {
            todo.completed = true
          }
        }
      } else if (trimmedLine.startsWith('- 备注：')) {
        todo.note = trimmedLine.substring(4).trim()
      }
    }
    
    i++
  }
  
  return i - 1
}

// 将JSON数据转换为Markdown文本
export function convertJsonToMarkdown(topics) {
  let markdown = ''
  
  topics.forEach(topic => {
    // 专题标题
    markdown += `# ${topic.name}\n`
    
    // 专题描述
    if (topic.description) {
      markdown += `${topic.description}\n`
    }
    markdown += '\n'
    
    topic.tasks.forEach(task => {
      // 任务标题
      markdown += `## ${task.name}\n`
      
      // 任务描述
      if (task.description) {
        markdown += `${task.description}\n`
      }
      markdown += '\n'
      
      // 待办事项
      task.todos.forEach(todo => {
        // 待办标题（一级列表，无缩进）
        markdown += `- ${todo.title}\n`
        
        // 进度（二级列表，2个空格缩进，始终显示进度）
        markdown += `  - 进度：${todo.progress}%\n`
        
        // 备注（二级列表，2个空格缩进，只有有备注时才导出）
        if (todo.note && todo.note.trim()) {
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