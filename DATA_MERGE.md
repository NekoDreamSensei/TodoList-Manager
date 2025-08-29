# 数据合并功能说明

本文档说明了TodoList系统的数据合并功能，该功能允许用户在导入数据时保留现有数据，并将新数据合并到现有数据中。

## 合并策略

### 1. 专题级别合并

#### 合并规则
- **名称匹配**：如果导入的专题名称与现有专题名称相同，则合并任务
- **名称不匹配**：如果导入的专题名称不存在，则作为新专题添加
- **描述更新**：导入的专题描述会更新现有专题的描述

#### 示例
```javascript
// 现有数据
现有专题: "学习Vue"
  - 任务: "基础语法"

// 导入数据
导入专题: "学习Vue"  // 名称匹配，合并任务
  - 任务: "组件开发"

// 合并结果
合并后: "学习Vue"
  - 任务: "基础语法"
  - 任务: "组件开发"
```

### 2. 任务级别合并

#### 合并规则
- **名称匹配**：如果导入的任务名称与现有任务名称相同，则合并待办事项
- **名称不匹配**：如果导入的任务名称不存在，则作为新任务添加
- **描述更新**：导入的任务描述会更新现有任务的描述

#### 示例
```javascript
// 现有数据
现有任务: "学习Vue基础"
  - 待办: "安装Vue CLI"

// 导入数据
导入任务: "学习Vue基础"  // 名称匹配，合并待办
  - 待办: "创建第一个项目"

// 合并结果
合并后: "学习Vue基础"
  - 待办: "安装Vue CLI"
  - 待办: "创建第一个项目"
```

### 3. 待办事项级别合并

#### 合并规则
- **标题匹配**：如果导入的待办标题与现有待办标题相同，则更新进度和备注
- **标题不匹配**：如果导入的待办标题不存在，则作为新待办添加
- **进度保留**：保留较高的进度值（取最大值）
- **备注更新**：导入的备注会更新现有待办的备注

#### 示例
```javascript
// 现有数据
现有待办: "编写Hello World组件"
  - 进度: 30%
  - 备注: "还在学习中"

// 导入数据
导入待办: "编写Hello World组件"  // 标题匹配，更新进度和备注
  - 进度: 50%
  - 备注: "已经完成基础部分"

// 合并结果
合并后: "编写Hello World组件"
  - 进度: 50%  // 保留较高进度
  - 备注: "已经完成基础部分"  // 更新备注
```

## 合并函数详解

### 1. mergeTopics(topics, newTopics)

```javascript
const mergeTopics = (existingTopics, newTopics) => {
  // 处理空数据情况
  if (!newTopics || newTopics.length === 0) return existingTopics
  if (!existingTopics || existingTopics.length === 0) return newTopics
  
  const merged = [...existingTopics]
  const existingNames = new Set(existingTopics.map(t => t.name))
  
  newTopics.forEach(newTopic => {
    if (existingNames.has(newTopic.name)) {
      // 名称匹配，合并任务
      const existingTopic = existingTopics.find(t => t.name === newTopic.name)
      if (existingTopic) {
        existingTopic.tasks = mergeTasks(existingTopic.tasks, newTopic.tasks)
        existingTopic.description = newTopic.description || existingTopic.description
      }
    } else {
      // 名称不匹配，添加新专题
      merged.push({
        ...newTopic,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9)
      })
    }
  })
  
  return merged
}
```

### 2. mergeTasks(existingTasks, newTasks)

```javascript
const mergeTasks = (existingTasks, newTasks) => {
  // 处理空数据情况
  if (!newTasks || newTasks.length === 0) return existingTasks
  if (!existingTasks || existingTasks.length === 0) return newTasks
  
  const merged = [...existingTasks]
  const existingNames = new Set(existingTasks.map(t => t.name))
  
  newTasks.forEach(newTask => {
    if (existingNames.has(newTask.name)) {
      // 名称匹配，合并待办
      const existingTask = existingTasks.find(t => t.name === newTask.name)
      if (existingTask) {
        existingTask.todos = mergeTodos(existingTask.todos, newTask.todos)
        existingTask.description = newTask.description || existingTask.description
      }
    } else {
      // 名称不匹配，添加新任务
      merged.push({
        ...newTask,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9)
      })
    }
  })
  
  return merged
}
```

### 3. mergeTodos(existingTodos, newTodos)

```javascript
const mergeTodos = (existingTodos, newTodos) => {
  // 处理空数据情况
  if (!newTodos || newTodos.length === 0) return existingTodos
  if (!existingTodos || existingTodos.length === 0) return newTodos
  
  const merged = [...existingTodos]
  const existingTitles = new Set(existingTodos.map(t => t.title))
  
  newTodos.forEach(newTodo => {
    if (existingTitles.has(newTodo.title)) {
      // 标题匹配，更新进度和备注
      const existingTodo = existingTodos.find(t => t.title === newTodo.title)
      if (existingTodo) {
        existingTodo.progress = Math.max(existingTodo.progress || 0, newTodo.progress || 0)
        existingTodo.note = newTodo.note || existingTodo.note
        existingTodo.description = newTodo.description || existingTodo.description
        existingTodo.completed = existingTodo.progress === 100 || newTodo.completed
      }
    } else {
      // 标题不匹配，添加新待办
      merged.push({
        ...newTodo,
        id: Date.now().toString() + Math.random().toString(36).substr(2, 9)
      })
    }
  })
  
  return merged
}
```

## 使用场景

### 1. 增量数据导入
- 用户可以先导入基础数据
- 后续导入补充数据时，不会丢失已有数据
- 适合分阶段构建TodoList

### 2. 数据备份恢复
- 用户可以从备份文件恢复部分数据
- 现有数据不会被覆盖
- 新数据会智能合并到现有结构中

### 3. 团队协作
- 不同成员可以分别导入各自负责的部分
- 系统自动合并所有成员的数据
- 避免数据冲突和丢失

### 4. 数据迁移
- 从其他系统迁移数据时
- 可以分批导入，逐步完善
- 保持数据的连续性和完整性

## 注意事项

### 1. ID生成
- 新添加的项目会生成新的唯一ID
- 避免ID冲突和数据重复
- 使用时间戳+随机字符串确保唯一性

### 2. 数据一致性
- 合并后的数据会立即保存到localStorage
- 确保数据的一致性和持久性
- 支持撤销和重新导入

### 3. 性能考虑
- 合并操作在内存中进行，性能良好
- 大量数据时建议分批导入
- 避免频繁的导入操作

### 4. 错误处理
- 导入失败时不会影响现有数据
- 提供详细的错误信息和处理建议
- 支持部分成功的数据导入

## 测试建议

### 1. 基础合并测试
1. 创建一些基础数据
2. 导入包含相同名称的测试数据
3. 验证数据是否正确合并

### 2. 边界情况测试
1. 测试空数据的合并
2. 测试大量数据的合并
3. 测试特殊字符和长文本的合并

### 3. 冲突处理测试
1. 测试同名项目的进度冲突
2. 测试描述和备注的更新
3. 测试ID的唯一性

### 4. 性能测试
1. 测试大量数据的合并性能
2. 测试频繁导入的性能影响
3. 测试内存使用情况

## 总结

数据合并功能为TodoList系统提供了强大的数据管理能力，用户可以：

- 保留现有数据，避免数据丢失
- 智能合并新数据，避免重复
- 灵活管理数据，支持增量更新
- 安全导入数据，确保数据完整性

这个功能特别适合需要频繁导入数据或需要保持数据连续性的用户场景。 