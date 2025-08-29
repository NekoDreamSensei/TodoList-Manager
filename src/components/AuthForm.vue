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
      
      <button type="submit" class="btn-submit">登录</button>
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
      
      <button type="submit" class="btn-submit">注册</button>
    </form>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const emit = defineEmits(['login', 'register'])

const activeTab = ref('login')
const errorMessage = ref('')

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    errorMessage.value = '请填写完整的登录信息'
    return
  }
  
  // 从localStorage获取用户数据
  const users = JSON.parse(localStorage.getItem('users') || '[]')
  const user = users.find(u => 
    u.username === loginForm.username && u.password === loginForm.password
  )
  
  if (user) {
    errorMessage.value = ''
    emit('login', { username: user.username })
  } else {
    errorMessage.value = '用户名或密码错误'
  }
}

const handleRegister = () => {
  if (!registerForm.username || !registerForm.password || !registerForm.confirmPassword) {
    errorMessage.value = '请填写完整的注册信息'
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }
  
  if (registerForm.password.length < 6) {
    errorMessage.value = '密码长度至少6位'
    return
  }
  
  // 检查用户名是否已存在
  const users = JSON.parse(localStorage.getItem('users') || '[]')
  if (users.find(u => u.username === registerForm.username)) {
    errorMessage.value = '用户名已存在'
    return
  }
  
  // 创建新用户
  const newUser = {
    username: registerForm.username,
    password: registerForm.password,
    createdAt: new Date().toISOString()
  }
  
  users.push(newUser)
  localStorage.setItem('users', JSON.stringify(users))
  
  // 初始化新用户的专题数据
  const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
  allTopics[newUser.username] = []
  localStorage.setItem('topics', JSON.stringify(allTopics))
  
  errorMessage.value = ''
  emit('register', { username: newUser.username })
}
</script>

<style scoped>
.auth-form {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  max-width: 400px;
  width: 100%;
}

.auth-tabs {
  display: flex;
  margin-bottom: 2rem;
  border-bottom: 1px solid #e2e8f0;
}

.tab-btn {
  flex: 1;
  padding: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #718096;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-btn:hover {
  color: #4a5568;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #2d3748;
}

.form-group input {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 1rem;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.error-message {
  background: #fed7d7;
  color: #c53030;
  padding: 1rem;
  border-radius: 6px;
  margin-top: 1rem;
  text-align: center;
  border: 1px solid #feb2b2;
}
</style> 