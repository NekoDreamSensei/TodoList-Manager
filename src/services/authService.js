// src/services/authService.js
import api from './api.js'

class AuthService {
  constructor() {
    this.currentUser = null
    this.token = null
    // 延迟初始化，确保在页面完全加载后执行
    setTimeout(() => {
      this.init()
    }, 0)
  }

  init() {
    console.log('=== AuthService 初始化开始 ===')
    console.log('当前时间:', new Date().toISOString())
    
    // 从sessionStorage恢复认证状态
    const userStr = sessionStorage.getItem('currentUser')
    const token = sessionStorage.getItem('authToken')
    
    console.log('从sessionStorage读取:', { 
      userStr: userStr ? '存在' : '不存在', 
      token: token ? '存在' : '不存在',
      userStrLength: userStr?.length || 0,
      tokenLength: token?.length || 0
    })
    
    if (userStr && token) {
      try {
        this.currentUser = JSON.parse(userStr)
        this.token = token
        console.log('认证状态已恢复:', this.currentUser?.username)
        console.log('当前用户对象:', this.currentUser)
        console.log('当前token:', this.token.substring(0, 20) + '...')
      } catch (error) {
        console.error('恢复认证状态失败:', error)
        this.clearAuth()
      }
    } else {
      console.log('没有找到存储的认证信息')
      console.log('userStr:', userStr)
      console.log('token:', token)
    }
    console.log('=== AuthService 初始化完成 ===')
  }

  isAuthenticated() {
    const authenticated = !!(this.currentUser && this.token)
    console.log('检查认证状态:', { 
      authenticated, 
      hasUser: !!this.currentUser, 
      hasToken: !!this.token,
      userUsername: this.currentUser?.username,
      tokenPreview: this.token ? this.token.substring(0, 20) + '...' : 'null'
    })
    return authenticated
  }

  getCurrentUser() {
    console.log('获取当前用户:', this.currentUser)
    return this.currentUser
  }

  getToken() {
    return this.token
  }

  setAuth(user, token) {
    this.currentUser = user
    this.token = token
    sessionStorage.setItem('currentUser', JSON.stringify(user))
    sessionStorage.setItem('authToken', token)
    console.log('认证状态已设置:', user?.username)
    console.log('存储的token:', token.substring(0, 20) + '...')
  }

  clearAuth() {
    this.currentUser = null
    this.token = null
    sessionStorage.removeItem('currentUser')
    sessionStorage.removeItem('authToken')
    console.log('认证状态已清除')
  }

  // 检查认证状态 - 修复版本
  async checkAuthStatus() {
    try {
      console.log('=== 开始检查认证状态 ===')
      console.log('当前token:', this.token ? '存在' : '不存在')
      console.log('当前用户:', this.currentUser)
      console.log('sessionStorage中的token:', sessionStorage.getItem('authToken'))
      
      // 确保token同步到sessionStorage
      if (this.token && !sessionStorage.getItem('authToken')) {
        console.log('同步token到sessionStorage...')
        sessionStorage.setItem('authToken', this.token)
      }
      
      const response = await api.get('/users/check-auth')
      console.log('=== 后端响应 ===')
      console.log('完整响应:', response)
      console.log('响应类型:', typeof response)
      console.log('响应success:', response.success)
      console.log('响应message:', response.message)
      console.log('响应data:', response.data)
      console.log('响应data类型:', typeof response.data)
      
      // 如果检查成功且返回了用户信息，更新本地状态
      if (response.success && response.data) {
        console.log('更新本地认证状态...')
        this.setAuth(response.data, this.token)
      }
      
      const result = {
        success: response.success,
        message: response.message,
        user: response.data
      }
      
      console.log('=== 返回结果 ===')
      console.log('最终结果:', result)
      
      return result
    } catch (error) {
      console.error('=== 检查认证状态失败 ===')
      console.error('错误对象:', error)
      console.error('错误消息:', error.message)
      console.error('错误响应:', error.response)
      console.error('错误状态码:', error.response?.status)
      console.error('错误数据:', error.response?.data)
      
      // 如果是403或401错误，说明token无效，清除本地状态
      if (error.response?.status === 403 || error.response?.status === 401) {
        console.log('检测到403/401错误，清除本地认证状态')
        this.clearAuth()
        return {
          success: false,
          message: '认证已过期，请重新登录',
          user: null
        }
      }
      
      // 其他错误也返回失败状态
      return {
        success: false,
        message: '检查认证状态失败: ' + (error.message || '未知错误'),
        user: null
      }
    }
  }

  // 获取当前用户信息
  async getCurrentUserInfo() {
    try {
      console.log('获取当前用户信息...')
      const response = await api.get('/users/me')
      console.log('当前用户信息:', response)
      return response
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 退出登录
  async logout() {
    try {
      console.log('开始退出登录...')
      await api.post('/users/logout')
      console.log('退出登录成功')
    } catch (error) {
      console.error('退出登录失败:', error)
      throw error
    } finally {
      this.clearAuth()
    }
  }
}

export const authService = new AuthService()