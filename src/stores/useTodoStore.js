// src/stores/useTodoStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { topicService } from '../services/topicService.js'
import { taskService } from '../services/taskService.js'
import { todoService } from '../services/todoService.js'
import { authService } from '../services/authService.js'

export const useTodoStore = defineStore('todo', () => {
  // 状态
  const topics = ref([])
  const selectedTopic = ref(null)
  const selectedTask = ref(null)
  const currentUser = ref(null)

  // 设置当前用户
  const setCurrentUser = (user) => {
    currentUser.value = user
  }

  // 加载用户专题数据
  const loadUserTopics = async (userId) => {
    if (!userId) return
    
    try {
      const userTopics = await topicService.getTopicsByUserId(userId)
      topics.value = userTopics
      
      console.log(`加载用户 ${userId} 的数据:`, {
        topicsCount: userTopics.length,
        totalTasks: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.length : 0), 0),
        totalTodos: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.reduce((s, task) => s + (task.todos ? task.todos.length : 0), 0) : 0), 0)
      })
    } catch (error) {
      console.error('加载用户专题数据失败:', error)
      throw error
    }
  }

  // 创建专题
  const createTopic = async (topicData) => {
    if (!currentUser.value?.id) {
      throw new Error('用户未登录')
    }
    
    try {
      const newTopic = await topicService.createTopic(topicData, currentUser.value.id)
      topics.value.push(newTopic)
      return newTopic
    } catch (error) {
      console.error('创建专题失败:', error)
      throw error
    }
  }

  // 更新专题
  const updateTopic = async (topicData) => {
    try {
      const updatedTopic = await topicService.updateTopic(topicData.id, topicData)
      
      // 更新本地状态
      const index = topics.value.findIndex(t => t.id === topicData.id)
      if (index !== -1) {
        topics.value[index] = updatedTopic
      }
      
      // 如果当前选中的专题被更新，同步更新
      if (selectedTopic.value && selectedTopic.value.id === topicData.id) {
        selectedTopic.value = updatedTopic
      }
      
      return updatedTopic
    } catch (error) {
      console.error('更新专题失败:', error)
      throw error
    }
  }

  // 删除专题
  const deleteTopic = async (topicId) => {
    try {
      await topicService.deleteTopic(topicId)
      
      // 从本地状态中移除
      const index = topics.value.findIndex(t => t.id === topicId)
      if (index !== -1) {
        topics.value.splice(index, 1)
      }
      
      // 如果当前选中的专题被删除，清除选中状态
      if (selectedTopic.value && selectedTopic.value.id === topicId) {
        selectedTopic.value = null
        selectedTask.value = null
      }
    } catch (error) {
      console.error('删除专题失败:', error)
      throw error
    }
  }

  // 创建任务
  const createTask = async (taskData, topicId) => {
    try {
      const newTask = await taskService.createTask(taskData, topicId)
      
      // 更新本地状态
      const topic = topics.value.find(t => t.id === topicId)
      if (topic) {
        if (!topic.tasks) {
          topic.tasks = []
        }
        topic.tasks.push(newTask)
      }
      
      return newTask
    } catch (error) {
      console.error('创建任务失败:', error)
      throw error
    }
  }

  // 更新任务
  const updateTask = async (taskData, topicId) => {
    try {
      const updatedTask = await taskService.updateTask(taskData.id, taskData)
      
      // 更新本地状态
      const topic = topics.value.find(t => t.id === topicId)
      if (topic && topic.tasks) {
        const taskIndex = topic.tasks.findIndex(t => t.id === taskData.id)
        if (taskIndex !== -1) {
          topic.tasks[taskIndex] = updatedTask
        }
      }
      
      // 如果当前选中的任务被更新，同步更新
      if (selectedTask.value && selectedTask.value.id === taskData.id) {
        selectedTask.value = updatedTask
      }
      
      return updatedTask
    } catch (error) {
      console.error('更新任务失败:', error)
      throw error
    }
  }

  // 删除任务
  const deleteTask = async (taskId, topicId) => {
    try {
      await taskService.deleteTask(taskId)
      
      // 从本地状态中移除
      const topic = topics.value.find(t => t.id === topicId)
      if (topic && topic.tasks) {
        const taskIndex = topic.tasks.findIndex(t => t.id === taskId)
        if (taskIndex !== -1) {
          topic.tasks.splice(taskIndex, 1)
        }
      }
      
      // 如果当前选中的任务被删除，清除选中状态
      if (selectedTask.value && selectedTask.value.id === taskId) {
        selectedTask.value = null
      }
    } catch (error) {
      console.error('删除任务失败:', error)
      throw error
    }
  }

  // 创建待办事项
  const createTodo = async (todoData, taskId, topicId) => {
    try {
      const newTodo = await todoService.createTodo(todoData, taskId)
      
      // 更新本地状态
      const topic = topics.value.find(t => t.id === topicId)
      if (topic && topic.tasks) {
        const task = topic.tasks.find(t => t.id === taskId)
        if (task) {
          if (!task.todos) {
            task.todos = []
          }
          task.todos.push(newTodo)
        }
      }
      
      return newTodo
    } catch (error) {
      console.error('创建待办失败:', error)
      throw error
    }
  }

  // 更新待办事项
  const updateTodo = async (todoData, taskId, topicId) => {
    try {
      const updatedTodo = await todoService.updateTodo(todoData.id, todoData)
      
      // 更新本地状态
      const topic = topics.value.find(t => t.id === topicId)
      if (topic && topic.tasks) {
        const task = topic.tasks.find(t => t.id === taskId)
        if (task && task.todos) {
          const todoIndex = task.todos.findIndex(t => t.id === todoData.id)
          if (todoIndex !== -1) {
            task.todos[todoIndex] = updatedTodo
          }
        }
      }
      
      return updatedTodo
    } catch (error) {
      console.error('更新待办失败:', error)
      throw error
    }
  }

  // 删除待办事项
  const deleteTodo = async (todoId, taskId, topicId) => {
    try {
      await todoService.deleteTodo(todoId)
      
      // 从本地状态中移除
      const topic = topics.value.find(t => t.id === topicId)
      if (topic && topic.tasks) {
        const task = topic.tasks.find(t => t.id === taskId)
        if (task && task.todos) {
          const todoIndex = task.todos.findIndex(t => t.id === todoId)
          if (todoIndex !== -1) {
            task.todos.splice(todoIndex, 1)
          }
        }
      }
    } catch (error) {
      console.error('删除待办失败:', error)
      throw error
    }
  }

  // 清除数据
  const clearData = () => {
    topics.value = []
    selectedTopic.value = null
    selectedTask.value = null
    currentUser.value = null
  }

  return {
    topics,
    selectedTopic,
    selectedTask,
    currentUser,
    setCurrentUser,
    loadUserTopics,
    createTopic,
    updateTopic,
    deleteTopic,
    createTask,
    updateTask,
    deleteTask,
    createTodo,
    updateTodo,
    deleteTodo,
    clearData
  }
})