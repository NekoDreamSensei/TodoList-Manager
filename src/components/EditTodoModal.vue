<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>编辑待办</h3>
        <button @click="$emit('close')" class="btn-close">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="edit-todo-title">待办标题</label>
          <input 
            id="edit-todo-title"
            v-model="form.title" 
            type="text" 
            required 
            placeholder="请输入待办标题"
            maxlength="100"
          />
        </div>
        
        <div class="form-group">
          <label for="edit-todo-progress">进度</label>
          <div class="progress-input">
            <input 
              id="edit-todo-progress"
              v-model.number="form.progress" 
              type="range" 
              min="0" 
              max="100" 
              step="5"
              class="progress-slider"
            />
            <span class="progress-value">{{ form.progress }}%</span>
          </div>
        </div>
        
        <div class="form-group">
          <label for="edit-todo-note">备注</label>
          <textarea 
            id="edit-todo-note"
            v-model="form.note" 
            placeholder="请输入备注（可选）"
            rows="2"
            maxlength="200"
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="$emit('close')" class="btn-cancel">
            取消
          </button>
          <button type="submit" class="btn-submit">
            保存修改
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'update'])

const form = reactive({
  id: '',
  title: '',
  progress: 0,
  note: '',
  completed: false
})

// 监听todo变化，更新表单
watch(() => props.todo, (newTodo) => {
  if (newTodo) {
    Object.assign(form, {
      id: newTodo.id,
      title: newTodo.title,
      progress: newTodo.progress,
      note: newTodo.note,
      completed: newTodo.completed
    })
  }
}, { immediate: true })

const handleSubmit = () => {
  if (!form.title.trim()) {
    return
  }
  
  emit('update', {
    id: form.id,
    title: form.title.trim(),
    progress: form.progress,
    note: form.note.trim(),
    completed: form.completed
  })
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
  min-height: 60px;
}

.progress-input {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.progress-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  cursor: pointer;
}

.progress-value {
  min-width: 50px;
  text-align: center;
  font-size: 0.875rem;
  color: #718096;
  font-weight: 500;
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