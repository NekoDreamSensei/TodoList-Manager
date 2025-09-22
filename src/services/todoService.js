// src/services/todoService.js
import api from './api.js'

export const todoService = {
  // 获取任务下的所有待办事项
  async getTodosByTaskId(taskId) {
    try {
      console.log('获取待办列表:', taskId)
      const response = await api.get(`/todos/task/${taskId}`)
      console.log('待办列表响应:', response)
      return response
    } catch (error) {
      console.error('获取待办列表失败:', error)
      throw new Error(error.response?.data?.message || '获取待办列表失败')
    }
  },

  // 根据ID获取待办事项
  async getTodoById(id) {
    try {
      console.log('获取待办详情:', id)
      const response = await api.get(`/todos/${id}`)
      console.log('待办详情响应:', response)
      return response
    } catch (error) {
      console.error('获取待办详情失败:', error)
      throw new Error(error.response?.data?.message || '获取待办详情失败')
    }
  },

  // 创建待办事项
  async createTodo(todoData, taskId) {
    try {
      console.log('创建待办:', { todoData, taskId })
      const response = await api.post(`/todos/task/${taskId}`, todoData)
      console.log('创建待办响应:', response)
      return response
    } catch (error) {
      console.error('创建待办失败:', error)
      throw new Error(error.response?.data?.message || '创建待办失败')
    }
  },

  // 更新待办事项
  async updateTodo(id, todoData) {
    try {
      console.log('更新待办:', { id, todoData })
      const response = await api.put(`/todos/${id}`, todoData)
      console.log('更新待办响应:', response)
      return response
    } catch (error) {
      console.error('更新待办失败:', error)
      throw new Error(error.response?.data?.message || '更新待办失败')
    }
  },

  // 删除待办事项
  async deleteTodo(id) {
    try {
      console.log('删除待办:', id)
      const response = await api.delete(`/todos/${id}`)
      console.log('删除待办响应:', response)
      return response
    } catch (error) {
      console.error('删除待办失败:', error)
      throw new Error(error.response?.data?.message || '删除待办失败')
    }
  }
}
