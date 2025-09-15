<template>
  <div class="auth-form">
    <div class="auth-tabs">
      <button 
        :class="['tab-btn', { active: activeTab === 'login' }]"
        @click="activeTab = 'login'"
      >
        登录
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'register' }]"
        @click="activeTab = 'register'"
      >
        注册
      </button>
    </div>

    <!-- 登录表单 -->
    <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="form">
      <div class="form-group">
        <label for="login-username">用户名</label>
        <input 
          id="login-username"
          v-model="loginForm.username" 
          type="text" 
          required 
          placeholder="请输入用户名"
        />
      </div>
      
      <div class="form-group">
        <label for="login-password">密码</label>
        <input 
          id="login-password"
          v-model="loginForm.password" 
          type="password" 
          required 
          placeholder="请输入密码"
        />
      </div>
      
      <button type="submit" class="btn-submit" :disabled="isLoading">
        {{ isLoading ? '登录中...' : '登录' }}
      </button>
    </form>

    <!-- 注册表单 -->
    <form v-else @submit.prevent="handleRegister" class="form">
      <div class="form-group">
        <label for="register-username">用户名</label>
        <input 
          id="register-username"
          v-model="registerForm.username" 
          type="text" 
          required 
          placeholder="请输入用户名"
        />
      </div>
      
      <div class="form-group">
        <label for="register-password">密码</label>
        <input 
          id="register-password"
          v-model="registerForm.password" 
          type="password" 
          required 
          placeholder="请输入密码"
        />
      </div>
      
      <div class="form-group">
        <label for="register-confirm">确认密码</label>
        <input 
          id="register-confirm"
          v-model="registerForm.confirmPassword" 
          type="password" 
          required 
          placeholder="请再次输入密码"
        />
      </div>
      
      <button type="submit" class="btn-submit" :disabled="isLoading">
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
    </form>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { userService } from '../services/userService.js'
import { authService } from '../services/authService.js'

const emit = defineEmits(['login', 'register'])

const activeTab = ref('login')
const errorMessage = ref('')
const isLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    errorMessage.value = '请填写完整的登录信息'
    return
  }
  
  isLoading.value = true
  errorMessage.value = ''
  
  try {
    console.log('开始登录...')
    const response = await userService.login({
      username: loginForm.username,
      password: loginForm.password
    })
    
    console.log('登录成功:', response)
    
    // 使用authService设置认证状态
    authService.setAuth(response.user, response.token)
    
    // 触发login事件
    emit('login', response.user)
    
  } catch (error) {
    console.error('登录失败:', error)
    errorMessage.value = error.message || '登录失败，请检查用户名和密码'
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.confirmPassword) {
    errorMessage.value = '请填写完整的注册信息'
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }
  
  isLoading.value = true
  errorMessage.value = ''
  
  try {
    console.log('开始注册...')
    const response = await userService.register({
      username: registerForm.username,
      password: registerForm.password
    })
    
    console.log('注册成功:', response)
    
    // 使用authService设置认证状态
    authService.setAuth(response.user, response.token)
    
    // 显示成功消息
    errorMessage.value = '注册成功！正在跳转...'
    
    // 延迟触发register事件
    setTimeout(() => {
      emit('register', response.user)
    }, 1500)
    
  } catch (error) {
    console.error('注册失败:', error)
    errorMessage.value = error.message || '注册失败，请检查用户名是否已存在'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.auth-form {
  width: 100%;
}

.auth-tabs {
  display: flex;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.tab-btn {
  flex: 1;
  padding: 0.75rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  transition: all 0.2s;
}

.tab-btn.active {
  color: #007bff;
  border-bottom-color: #007bff;
}

.tab-btn:hover {
  color: #007bff;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.btn-submit {
  padding: 0.75rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.2s;
  margin-top: 1rem;
}

.btn-submit:hover:not(:disabled) {
  background: #0056b3;
}

.btn-submit:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  font-size: 0.9rem;
}
</style>