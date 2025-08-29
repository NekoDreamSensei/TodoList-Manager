# TodoList 数据存储机制

本文档说明了TodoList系统的数据存储机制和故障排除方法。

## 存储架构

### 1. 数据结构
```javascript
localStorage = {
  // 用户认证信息
  'currentUser': {
    username: '用户名',
    password: '密码',
    createdAt: '创建时间'
  },
  
  // 所有用户的专题数据
  'topics': {
    '用户名1': [
      {
        id: '专题ID',
        name: '专题名称',
        description: '专题描述',
        createdAt: '创建时间',
        tasks: [
          {
            id: '任务ID',
            name: '任务名称',
            description: '任务描述',
            createdAt: '创建时间',
            todos: [
              {
                id: '待办ID',
                title: '待办标题',
                description: '待办描述',
                completed: false,
                progress: 0,
                note: '备注',
                createdAt: '创建时间'
              }
            ]
          }
        ]
      }
    ],
    '用户名2': [...]
  }
}
```

### 2. 存储时机

#### 自动保存
- **监听器**：`watch(topics, saveData, { deep: true })`
- **触发条件**：任何专题、任务、待办的变化
- **保存内容**：当前用户的完整数据

#### 手动保存
- **创建操作**：新建专题、任务、待办后
- **修改操作**：编辑、删除、状态变更后
- **导入操作**：导入JSON或Markdown后

### 3. 数据加载

#### 登录时加载
```javascript
const handleLogin = (user) => {
  currentUser.value = user
  localStorage.setItem('currentUser', JSON.stringify(user))
  loadUserTopics()  // 加载用户数据
}
```

#### 页面刷新时加载
```javascript
onMounted(() => {
  loadData()  // 检查登录状态并加载数据
})
```

## 故障排除

### 1. 数据丢失问题

#### 可能原因
- 浏览器清除了localStorage
- 用户切换了浏览器或设备
- 数据存储失败

#### 检查方法
1. 打开浏览器开发者工具
2. 查看Console中的调试信息
3. 检查Application > Local Storage

#### 调试信息
```javascript
// 保存数据时的日志
console.log(`保存用户 ${username} 的数据:`, {
  topicsCount: 专题数量,
  totalTasks: 任务总数,
  totalTodos: 待办总数,
  localStorageSize: 存储大小
})

// 加载数据时的日志
console.log(`加载用户 ${username} 的数据:`, {
  topicsCount: 专题数量,
  totalTasks: 任务总数,
  totalTodos: 待办总数,
  localStorageKeys: 存储键列表,
  allTopicsData: 完整数据
})
```

### 2. 数据不保存问题

#### 检查点
1. 用户是否已登录
2. `saveData()` 函数是否被调用
3. localStorage是否有写入权限

#### 解决方案
1. 确保用户已登录
2. 检查Console是否有错误信息
3. 尝试手动导出数据备份

### 3. 多用户数据隔离

#### 机制
- 每个用户的数据存储在 `topics[username]` 中
- 登录时只加载当前用户的数据
- 其他用户的数据不会被访问或修改

#### 验证方法
```javascript
// 检查localStorage中的数据结构
const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
console.log('所有用户:', Object.keys(allTopics))
console.log('当前用户数据:', allTopics[currentUser.username])
```

## 最佳实践

### 1. 数据备份
- 定期导出JSON或Markdown文件
- 重要数据建议导出备份
- 可以导入到其他设备

### 2. 数据清理
- 使用"清除数据"功能清理当前用户数据
- 退出登录不会删除数据
- 清除浏览器数据会丢失所有数据

### 3. 性能优化
- 大量数据时考虑分批处理
- 避免频繁的深度监听
- 合理使用数据缓存

## 技术细节

### 1. 存储限制
- localStorage通常限制为5-10MB
- 数据以JSON字符串形式存储
- 支持复杂嵌套对象

### 2. 兼容性
- 支持所有现代浏览器
- 移动端浏览器支持良好
- 不支持IE8及以下版本

### 3. 安全性
- 数据存储在本地，不上传服务器
- 密码以明文存储（仅用于演示）
- 生产环境建议加密存储

## 联系支持

如果遇到数据存储问题：
1. 检查Console调试信息
2. 尝试重新登录
3. 导出数据备份
4. 清除数据重新开始 