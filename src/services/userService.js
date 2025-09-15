// src/services/userService.js
import api from './api.js'

export const userService = {
  // 用户登录
  async login(credentials) {
    try {
      console.log('发送登录请求:', credentials)
      const response = await api.post('/users/login', credentials)
      console.log('登录响应:', response)
      return response
    } catch (error) {
      console.error('登录请求失败:', error)
      throw new Error(error.response?.data?.message || '登录失败，请检查网络连接')
    }
  },

  // 用户注册
  async register(userData) {
    try {
      console.log('发送注册请求:', userData)
      const response = await api.post('/users/register', userData)
      console.log('注册响应:', response)
      return response
    } catch (error) {
      console.error('注册请求失败:', error)
      throw new Error(error.response?.data?.message || '注册失败，请检查网络连接')
    }
  },

  // 用户退出登录
  async logout() {
    try {
      console.log('发送退出登录请求')
      const response = await api.post('/users/logout')
      console.log('退出登录响应:', response)
      return response
    } catch (error) {
      console.error('退出登录请求失败:', error)
      throw new Error(error.response?.data?.message || '退出登录失败，请检查网络连接')
    }
  },

  // 检查认证状态
  async checkAuthStatus() {
    try {
      console.log('发送检查认证状态请求')
      const response = await api.get('/users/check-auth')
      console.log('检查认证状态响应:', response)
      return response
    } catch (error) {
      console.error('检查认证状态请求失败:', error)
      throw new Error(error.response?.data?.message || '检查认证状态失败，请检查网络连接')
    }
  },

  // 获取当前用户信息
  async getCurrentUser() {
    try {
      console.log('发送获取当前用户信息请求')
      const response = await api.get('/users/me')
      console.log('获取当前用户信息响应:', response)
      return response
    } catch (error) {
      console.error('获取当前用户信息请求失败:', error)
      throw new Error(error.response?.data?.message || '获取用户信息失败，请检查网络连接')
    }
  }
}