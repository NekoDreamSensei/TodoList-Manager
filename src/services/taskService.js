import api from './api.js'

export const taskService = {
  // 获取专题下的所有任务
  async getTasksByTopicId(topicId) {
    try {
      const response = await api.get(`/api/tasks/topic/${topicId}`)
      return response
    } catch (error) {
      throw new Error(`获取任务列表失败: ${error.message}`)
    }
  },

  // 根据ID获取任务
  async getTaskById(id) {
    try {
      const response = await api.get(`/api/tasks/${id}`)
      return response
    } catch (error) {
      throw new Error(`获取任务信息失败: ${error.message}`)
    }
  },

  // 创建任务
  async createTask(topicId, taskData) {
    try {
      const response = await api.post(`/api/tasks/topic/${topicId}`, taskData)
      return response
    } catch (error) {
      throw new Error(`创建任务失败: ${error.message}`)
    }
  },

  // 更新任务
  async updateTask(id, taskData) {
    try {
      const response = await api.put(`/api/tasks/${id}`, taskData)
      return response
    } catch (error) {
      throw new Error(`更新任务失败: ${error.message}`)
    }
  },

  // 删除任务
  async deleteTask(id) {
    try {
      const response = await api.delete(`/api/tasks/${id}`)
      return response
    } catch (error) {
      throw new Error(`删除任务失败: ${error.message}`)
    }
  }
} 