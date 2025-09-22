// src/services/api.js
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://112.74.41.106:5173/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从sessionStorage获取token
    const token = sessionStorage.getItem('authToken')
    console.log('API请求拦截器 - 获取token:', token ? '存在' : '不存在')
    console.log('API请求拦截器 - token内容:', token ? token.substring(0, 20) + '...' : 'null')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('API请求拦截器 - 已添加Authorization头:', `Bearer ${token.substring(0, 20)}...`)
    } else {
      console.log('API请求拦截器 - 没有token，不添加Authorization头')
    }
    
    console.log('API请求:', config.method?.toUpperCase(), config.url)
    console.log('API请求头:', config.headers)
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    console.log('API响应成功:', response.status, response.config.url)
    return response.data
  },
  (error) => {
    console.error('API响应错误:', error.response?.status, error.config?.url)
    console.error('API响应错误详情:', error.response)
    
    if (error.response?.status === 401) {
      // 清除认证状态
      sessionStorage.removeItem('currentUser')
      sessionStorage.removeItem('authToken')
      console.log('认证已过期，请重新登录')
    }
    
    return Promise.reject(error)
  }
)

export default api