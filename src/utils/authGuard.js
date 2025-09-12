/**
 * 认证守卫工具
 * 
 * 提供用户认证状态检查和权限验证功能
 */

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function checkAuth() {
  const currentUser = sessionStorage.getItem('currentUser')
  const authToken = sessionStorage.getItem('authToken')
  
  console.log('checkAuth 检查:')
  console.log('currentUser:', currentUser)
  console.log('authToken:', authToken)
  
  const isAuthenticated = !!(currentUser && authToken)
  console.log('认证状态:', isAuthenticated)
  
  return isAuthenticated
}

/**
 * 要求用户必须登录
 * @returns {boolean} 是否已登录
 */
export function requireAuth() {
  if (!checkAuth()) {
    // 清除可能存在的无效数据
    sessionStorage.removeItem('currentUser')
    sessionStorage.removeItem('authToken')
    
    // 重定向到登录页面
    window.location.href = '/'
    return false
  }
  return true
}

/**
 * 获取当前用户信息
 * @returns {Object|null} 用户信息或null
 */
export function getCurrentUser() {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      sessionStorage.removeItem('currentUser')
      return null
    }
  }
  return null
}

/**
 * 获取认证token
 * @returns {string|null} token或null
 */
export function getAuthToken() {
  return sessionStorage.getItem('authToken')
}

/**
 * 清除认证信息
 */
export function clearAuth() {
  sessionStorage.removeItem('currentUser')
  sessionStorage.removeItem('authToken')
}
