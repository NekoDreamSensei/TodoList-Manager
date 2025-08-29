<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>创建新专题</h3>
        <button @click="$emit('close')" class="btn-close">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="topic-name">专题名称</label>
          <input 
            id="topic-name"
            v-model="form.name" 
            type="text" 
            required 
            placeholder="请输入专题名称"
            maxlength="50"
          />
        </div>
        
        <div class="form-group">
          <label for="topic-description">专题描述</label>
          <textarea 
            id="topic-description"
            v-model="form.description" 
            placeholder="请输入专题描述（可选）"
            rows="3"
            maxlength="200"
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="$emit('close')" class="btn-cancel">
            取消
          </button>
          <button type="submit" class="btn-submit">
            创建专题
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['close', 'create'])

const form = reactive({
  name: '',
  description: ''
})

const handleSubmit = () => {
  if (!form.name.trim()) {
    return
  }
  
  emit('create', {
    name: form.name.trim(),
    description: form.description.trim()
  })
  
  // 重置表单
  form.name = ''
  form.description = ''
}

const handleOverlayClick = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.5rem 0;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 1rem;
}

.modal-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-close:hover {
  background: #f7fafc;
  color: #4a5568;
}

.modal-form {
  padding: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.form-group label {
  font-weight: 500;
  color: #2d3748;
}

.form-group input,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.btn-cancel {
  background: #718096;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #4a5568;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style> 