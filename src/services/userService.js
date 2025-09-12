import api from './api.js'

export const userService = {
  // 用户登录
  async login(credentials) {
    try {
      const response = await api.post('/api/users/login', credentials)
      return response
    } catch (error) {
      throw new Error(`登录失败: ${error.message}`)
    }
  },

  // 用户注册
  async register(userData) {
    try {
      const response = await api.post('/api/users/register', userData)
      return response
    } catch (error) {
      throw new Error(`注册失败: ${error.message}`)
    }
  },

  // 用户注销
  async logout() {
    try {
      const response = await api.post('/api/users/logout')
      return response
    } catch (error) {
      throw new Error(`注销失败: ${error.message}`)
    }
  },

  // 获取所有用户
  async getAllUsers() {
    try {
      const response = await api.get('/api/users')
      return response
    } catch (error) {
      throw new Error(`获取用户列表失败: ${error.message}`)
    }
  },

  // 根据ID获取用户
  async getUserById(id) {
    try {
      const response = await api.get(`/api/users/${id}`)
      return response
    } catch (error) {
      throw new Error(`获取用户信息失败: ${error.message}`)
    }
  },

  // 更新用户信息
  async updateUser(id, userData) {
    try {
      const response = await api.put(`/api/users/${id}`, userData)
      return response
    } catch (error) {
      throw new Error(`更新用户信息失败: ${error.message}`)
    }
  },

  // 删除用户
  async deleteUser(id) {
    try {
      const response = await api.delete(`/api/users/${id}`)
      return response
    } catch (error) {
      throw new Error(`删除用户失败: ${error.message}`)
    }
  }
} 