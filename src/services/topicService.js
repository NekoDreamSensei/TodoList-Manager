import api from './api.js'

// 专题相关API
export const topicService = {
  // 获取用户的所有专题
  async getTopicsByUserId(userId) {
    try {
      const response = await api.get(`/api/topics/user/${userId}`)
      return response
    } catch (error) {
      throw new Error(`获取专题列表失败: ${error.message}`)
    }
  },

  // 根据ID获取专题
  async getTopicById(id) {
    try {
      const response = await api.get(`/api/topics/${id}`)
      return response
    } catch (error) {
      throw new Error(`获取专题信息失败: ${error.message}`)
    }
  },

  // 创建专题
  async createTopic(userId, topicData) {
    try {
      const response = await api.post(`/api/topics/user/${userId}`, topicData)
      return response
    } catch (error) {
      throw new Error(`创建专题失败: ${error.message}`)
    }
  },

  // 更新专题
  async updateTopic(id, topicData) {
    try {
      const response = await api.put(`/api/topics/${id}`, topicData)
      return response
    } catch (error) {
      throw new Error(`更新专题失败: ${error.message}`)
    }
  },

  // 删除专题
  async deleteTopic(id) {
    try {
      const response = await api.delete(`/api/topics/${id}`)
      return response
    } catch (error) {
      throw new Error(`删除专题失败: ${error.message}`)
    }
  }
} 