import { createRouter, createWebHistory } from "vue-router"
import { authService } from "../services/authService.js"

// 导入页面组件
import LoginPage from "../views/LoginPage.vue"
import DashboardPage from "../views/DashboardPage.vue"
import TestPage from "../views/TestPage.vue"

const routes = [
  {
    path: "/",
    redirect: "/login"
  },
  {
    path: "/login",
    name: "Login",
    component: LoginPage,
    meta: { 
      requiresAuth: false,
      title: "登录页面"
    }
  },
  {
    path: "/dashboard",
    name: "Dashboard", 
    component: DashboardPage,
    meta: { 
      requiresAuth: true,
      title: "仪表板"
    }
  },
  {
    path: "/test",
    name: "Test", 
    component: TestPage,
    meta: { 
      requiresAuth: true,
      title: "测试页面"
    }
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/login"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 增强的路由守卫 - 带详细提示
router.beforeEach(async (to, from, next) => {
  console.log("🛡️ ===== 路由守卫开始 =====")
  console.log("📍 目标路由:", to.name, to.path)
  console.log("📍 来源路由:", from.name, from.path)
  console.log("🔐 需要认证:", to.meta.requiresAuth)
  
  // 检查认证状态
  const isAuthenticated = authService.isAuthenticated()
  const currentUser = authService.getCurrentUser()
  const hasToken = !!authService.getToken()
  
  console.log("🔍 认证状态检查:")
  console.log("  - 是否已认证:", isAuthenticated)
  console.log("  - 当前用户:", currentUser?.username || "无")
  console.log("  - 是否有token:", hasToken)
  console.log("  - Token内容:", hasToken ? authService.getToken().substring(0, 20) + "..." : "无")
  
  // 如果需要认证
  if (to.meta.requiresAuth) {
    console.log("🔒 访问受保护路由，检查认证状态...")
    
    if (isAuthenticated) {
      console.log("✅ 认证通过，允许访问")
      next()
    } else {
      console.log("❌ 未认证，重定向到登录页")
      console.log("🔄 重定向: /dashboard -> /login")
      
      // 显示用户提示
      if (typeof window !== "undefined") {
        // 延迟显示提示，确保页面已经跳转
        setTimeout(() => {
          alert("🔒 访问被拒绝\n\n您需要先登录才能访问仪表板页面。\n\n请先登录您的账户。")
        }, 100)
      }
      
      next("/login")
    }
  } 
  // 如果访问登录页但已认证
  else if (to.name === "Login" && isAuthenticated) {
    console.log("🔄 已登录用户访问登录页，重定向到仪表板")
    console.log("🔄 重定向: /login -> /dashboard")
    
    // 显示用户提示
    if (typeof window !== "undefined") {
      setTimeout(() => {
        alert("✅ 您已登录\n\n检测到您已经登录，正在跳转到仪表板...")
      }, 100)
    }
    
    next("/dashboard")
  } 
  // 其他情况直接通过
  else {
    console.log("✅ 路由守卫通过，允许访问")
    next()
  }
  
  console.log("🛡️ ===== 路由守卫结束 =====\n")
})

// 路由守卫后置钩子 - 用于页面加载完成后的提示
router.afterEach((to, from) => {
  console.log("🎯 路由跳转完成:")
  console.log("  - 到达页面:", to.name, to.path)
  console.log("  - 页面标题:", to.meta.title || to.name)
  console.log("  - 时间:", new Date().toLocaleString())
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - TodoList管理系统`
  }
})

export default router