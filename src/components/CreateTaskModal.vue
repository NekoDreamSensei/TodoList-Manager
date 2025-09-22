<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>新建任务</h3>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-body">
        <div class="form-group">
          <label for="taskTitle">任务标题 *</label>
          <input
            id="taskTitle"
            v-model="formData.title"
            type="text"
            required
            placeholder="请输入任务标题"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label for="taskDescription">任务描述</label>
          <textarea
            id="taskDescription"
            v-model="formData.description"
            placeholder="请输入任务描述（可选）"
            class="form-textarea"
            rows="3"
          ></textarea>
        </div>
        
        <div class="form-group">
          <label for="taskStatus">任务状态</label>
          <select
            id="taskStatus"
            v-model="formData.status"
            class="form-select"
          >
            <option value="待开始">待开始</option>
            <option value="进行中">进行中</option>
            <option value="已完成">已完成</option>
            <option value="已暂停">已暂停</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="taskPriority">优先级</label>
          <select
            id="taskPriority"
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
          <label for="taskDueDate">截止日期</label>
          <input
            id="taskDueDate"
            v-model="formData.dueDate"
            type="datetime-local"
            class="form-input"
          />
        </div>
        
        <div class="modal-actions">
          <button type="button" @click="closeModal" class="btn-cancel">取消</button>
          <button type="submit" class="btn-primary">创建任务</button>
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
  description: '',
  status: '待开始',
  priority: '普通',
  dueDate: ''
})

const handleSubmit = () => {
  if (!formData.title.trim()) {
    alert('请输入任务标题')
    return
  }
  
  // 处理日期格式
  const taskData = { ...formData }
  if (taskData.dueDate) {
    taskData.dueDate = new Date(taskData.dueDate).toISOString()
  } else {
    taskData.dueDate = null
  }
  
  emit('create', taskData)
  resetForm()
}

const closeModal = () => {
  emit('close')
  resetForm()
}

const resetForm = () => {
  formData.title = ''
  formData.description = ''
  formData.status = '待开始'
  formData.priority = '普通'
  formData.dueDate = ''
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
