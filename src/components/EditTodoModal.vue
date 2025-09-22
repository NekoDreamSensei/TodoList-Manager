<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>编辑待办</h3>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-body">
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
          <label for="todoPriority">优先级</label>
          <select
            id="todoPriority"
            v-model="formData.priority"
            class="form-select"
          >
            <option value="低">低</option>
            <option value="普通">普通</option>
            <option value="高">高</option>
            <option value="紧急">紧急</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="todoCompleted">完成状态</label>
          <div class="checkbox-container">
            <input
              id="todoCompleted"
              v-model="formData.isCompleted"
              type="checkbox"
              class="checkbox-input"
            />
            <label for="todoCompleted" class="checkbox-label">已完成</label>
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
          <button type="submit" class="btn-primary">更新待办</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'update'])

const formData = reactive({
  content: '',
  priority: '普通',
  isCompleted: false,
  note: ''
})

onMounted(() => {
  // 初始化表单数据
  formData.content = props.todo.content || ''
  formData.priority = props.todo.priority || '普通'
  formData.isCompleted = Boolean(props.todo.isCompleted)
  formData.note = props.todo.note || ''
})

const handleSubmit = () => {
  if (!formData.content.trim()) {
    alert('请输入待办内容')
    return
  }
  
  // 确保 isCompleted 是 Boolean 类型
  const todoData = {
    ...props.todo,
    content: formData.content.trim(),
    priority: formData.priority,
    isCompleted: Boolean(formData.isCompleted),
    note: formData.note.trim()
  }
  
  emit('update', todoData)
  resetForm()
}

const closeModal = () => {
  emit('close')
  resetForm()
}

const resetForm = () => {
  formData.content = ''
  formData.priority = '普通'
  formData.isCompleted = false
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
.form-textarea,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-input {
  width: auto;
  margin: 0;
}

.checkbox-label {
  margin: 0;
  cursor: pointer;
  user-select: none;
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
