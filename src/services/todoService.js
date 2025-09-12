import api from './api.js'

export const todoService = {
  // 获取任务下的所有待办
  async getTodosByTaskId(taskId) {
    try {
      const response = await api.get(`/api/todos/task/${taskId}`)
      return response
    } catch (error) {
      throw new Error(`获取待办列表失败: ${error.message}`)
    }
  },

  // 根据ID获取待办
  async getTodoById(id) {
    try {
      const response = await api.get(`/api/todos/${id}`)
      return response
    } catch (error) {
      throw new Error(`获取待办信息失败: ${error.message}`)
    }
  },

  // 创建待办
  async createTodo(taskId, todoData) {
    try {
      const response = await api.post(`/api/todos/task/${taskId}`, todoData)
      return response
    } catch (error) {
      throw new Error(`创建待办失败: ${error.message}`)
    }
  },

  // 更新待办
  async updateTodo(id, todoData) {
    try {
      const response = await api.put(`/api/todos/${id}`, todoData)
      return response
    } catch (error) {
      throw new Error(`更新待办失败: ${error.message}`)
    }
  },

  // 删除待办
  async deleteTodo(id) {
    try {
      const response = await api.delete(`/api/todos/${id}`)
      return response
    } catch (error) {
      throw new Error(`删除待办失败: ${error.message}`)
    }
  }
} 