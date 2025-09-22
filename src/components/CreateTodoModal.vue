<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>新建待办</h3>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-body">
        <div class="form-group">
          <label for="todoTitle">待办标题 *</label>
          <input
            id="todoTitle"
            v-model="formData.title"
            type="text"
            required
            placeholder="请输入待办标题"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label for="todoContent">待办内容 *</label>
          <input
            id="todoContent"
            v-model="formData.content"
            type="text"
            required
            placeholder="请输入待办内容"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label for="todoProgress">初始进度</label>
          <div class="progress-container">
            <input
              id="todoProgress"
              v-model="formData.progress"
              type="range"
              min="0"
              max="100"
              class="progress-slider"
            />
            <span class="progress-value">{{ formData.progress }}%</span>
          </div>
        </div>
        
        <div class="form-group">
          <label for="todoNote">备注</label>
          <textarea
            id="todoNote"
            v-model="formData.note"
            placeholder="请输入备注（可选）"
            class="form-textarea"
            rows="3"
          ></textarea>
        </div>
        
        <div class="modal-actions">
          <button type="button" @click="closeModal" class="btn-cancel">取消</button>
          <button type="submit" class="btn-primary">创建待办</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const emit = defineEmits(['close', 'create'])

const formData = reactive({
  title: '',
  content: '',
  progress: 0,
  note: ''
})

const handleSubmit = () => {
  if (!formData.title.trim()) {
    alert('请输入待办标题')
    return
  }
  
  if (!formData.content.trim()) {
    alert('请输入待办内容')
    return
  }
  
  // 确保字段映射正确
  const todoData = {
    title: formData.title.trim(),
    content: formData.content.trim(),
    progress: parseInt(formData.progress),
    isCompleted: formData.progress >= 100,
    completed: formData.progress >= 100,
    note: formData.note.trim()
  }
  
  emit('create', todoData)
  resetForm()
}

const closeModal = () => {
  emit('close')
  resetForm()
}

const resetForm = () => {
  formData.title = ''
  formData.content = ''
  formData.progress = 0
  formData.note = ''
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
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f7fafc;
  color: #2d3748;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4a5568;
  font-weight: 500;
  font-size: 0.875rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
  appearance: none;
}

.progress-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #4299e1;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #4299e1;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-value {
  min-width: 50px;
  text-align: center;
  font-size: 0.875rem;
  color: #718096;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel,
.btn-primary {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-cancel {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.btn-cancel:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.btn-primary {
  background: #4299e1;
  color: white;
}

.btn-primary:hover {
  background: #3182ce;
}

.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}
</style>
