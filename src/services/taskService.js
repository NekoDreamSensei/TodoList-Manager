// src/services/taskService.js
import api from './api.js'

export const taskService = {
  // 获取主题下的所有任务
  async getTasksByTopicId(topicId) {
    try {
      console.log('获取任务列表:', topicId)
      const response = await api.get(`/tasks/topic/${topicId}`)
      console.log('任务列表响应:', response)
      return response
    } catch (error) {
      console.error('获取任务列表失败:', error)
      throw new Error(error.response?.data?.message || '获取任务列表失败')
    }
  },

  // 根据ID获取任务
  async getTaskById(id) {
    try {
      console.log('获取任务详情:', id)
      const response = await api.get(`/tasks/${id}`)
      console.log('任务详情响应:', response)
      return response
    } catch (error) {
      console.error('获取任务详情失败:', error)
      throw new Error(error.response?.data?.message || '获取任务详情失败')
    }
  },

  // 创建任务
  async createTask(taskData, topicId) {
    try {
      console.log('创建任务:', { taskData, topicId })
      const response = await api.post(`/tasks/topic/${topicId}`, taskData)
      console.log('创建任务响应:', response)
      return response
    } catch (error) {
      console.error('创建任务失败:', error)
      throw new Error(error.response?.data?.message || '创建任务失败')
    }
  },

  // 更新任务
  async updateTask(id, taskData) {
    try {
      console.log('更新任务:', { id, taskData })
      const response = await api.put(`/tasks/${id}`, taskData)
      console.log('更新任务响应:', response)
      return response
    } catch (error) {
      console.error('更新任务失败:', error)
      throw new Error(error.response?.data?.message || '更新任务失败')
    }
  },

  // 删除任务
  async deleteTask(id) {
    try {
      console.log('删除任务:', id)
      const response = await api.delete(`/tasks/${id}`)
      console.log('删除任务响应:', response)
      return response
    } catch (error) {
      console.error('删除任务失败:', error)
      throw new Error(error.response?.data?.message || '删除任务失败')
    }
  }
}
