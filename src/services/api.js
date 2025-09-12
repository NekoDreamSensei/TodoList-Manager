import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 自动添加token
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一错误处理
api.interceptors.response.use(
  (response) => response.data, // 直接返回data
  (error) => {
    console.error('API请求错误:', error)
    
    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response
      let message = data?.message || '请求失败'
      
      switch (status) {
        case 401:
          // 未授权，清除登录状态并重定向
          sessionStorage.removeItem('currentUser')
          sessionStorage.removeItem('authToken')
          message = '登录已过期，请重新登录'
          // 延迟重定向，让用户看到错误信息
          setTimeout(() => {
            window.location.href = '/'
          }, 2000)
          break
        case 403:
          message = '没有权限访问此资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 409:
          message = '数据冲突，请检查后重试'
          break
        case 422:
          message = '数据验证失败'
          break
        case 500:
          message = '服务器内部错误，请稍后重试'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务暂时不可用，请稍后重试'
          break
        default:
          message = data?.message || `请求失败 (${status})`
      }
      
      throw new Error(message)
    } else if (error.request) {
      // 网络错误
      throw new Error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      throw new Error(error.message || '请求失败')
    }
  }
)

export default api 