// src/services/topicService.js
import api from './api.js'

export const topicService = {
  // 获取用户的所有专题
  async getTopicsByUserId(userId) {
    try {
      console.log('获取用户专题列表:', userId)
      const response = await api.get(`/topics/user/${userId}`)
      console.log('专题列表响应:', response)
      return response
    } catch (error) {
      console.error('获取专题列表失败:', error)
      throw new Error(error.response?.data?.message || '获取专题列表失败')
    }
  },

  // 根据ID获取专题
  async getTopicById(id) {
    try {
      console.log('获取专题详情:', id)
      const response = await api.get(`/topics/${id}`)
      console.log('专题详情响应:', response)
      return response
    } catch (error) {
      console.error('获取专题详情失败:', error)
      throw new Error(error.response?.data?.message || '获取专题详情失败')
    }
  },

  // 创建专题
  async createTopic(topicData, userId) {
    try {
      console.log('创建专题:', { topicData, userId })
      const response = await api.post(`/topics/user/${userId}`, topicData)
      console.log('创建专题响应:', response)
      return response
    } catch (error) {
      console.error('创建专题失败:', error)
      throw new Error(error.response?.data?.message || '创建专题失败')
    }
  },

  // 更新专题
  async updateTopic(id, topicData) {
    try {
      console.log('更新专题:', { id, topicData })
      const response = await api.put(`/topics/${id}`, topicData)
      console.log('更新专题响应:', response)
      return response
    } catch (error) {
      console.error('更新专题失败:', error)
      throw new Error(error.response?.data?.message || '更新专题失败')
    }
  },

  // 删除专题
  async deleteTopic(id) {
    try {
      console.log('删除专题:', id)
      const response = await api.delete(`/topics/${id}`)
      console.log('删除专题响应:', response)
      return response
    } catch (error) {
      console.error('删除专题失败:', error)
      throw new Error(error.response?.data?.message || '删除专题失败')
    }
  }
}
