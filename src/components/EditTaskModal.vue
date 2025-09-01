<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>管理任务</h3>
        <button @click="closeModal" class="btn-close">×</button>
      </div>
      
      <div class="modal-body">
        <div class="form-group">
          <label for="taskName">任务名称</label>
          <input 
            id="taskName"
            v-model="formData.name" 
            type="text" 
            placeholder="请输入任务名称"
            class="form-input"
            maxlength="50"
          />
        </div>
        
        <div class="form-group">
          <label for="taskDescription">任务描述</label>
          <textarea 
            id="taskDescription"
            v-model="formData.description" 
            placeholder="请输入任务描述（可选）"
            class="form-textarea"
            rows="4"
            maxlength="500"
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button @click="saveTask" class="btn-primary" :disabled="!formData.name.trim()">
            保存更改
          </button>
          <button @click="deleteTask" class="btn-danger">
            删除任务
          </button>
          <button @click="closeModal" class="btn-secondary">
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'update', 'delete'])

const formData = ref({
  name: '',
  description: ''
})

// 初始化表单数据
onMounted(() => {
  formData.value = {
    name: props.task.name || '',
    description: props.task.description || ''
  }
})

const closeModal = () => {
  emit('close')
}

const saveTask = () => {
  if (!formData.value.name.trim()) {
    alert('请输入任务名称')
    return
  }
  
  emit('update', {
    ...props.task,
    name: formData.value.name.trim(),
    description: formData.value.description.trim()
  })
  
  closeModal()
}

const deleteTask = () => {
  if (confirm('确定要删除这个任务吗？这将删除任务下的所有待办事项，此操作不可恢复！')) {
    emit('delete', props.task.id)
    closeModal()
  }
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.btn-close:hover {
  background: #f8f9fa;
  color: #333;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.btn-primary,
.btn-danger,
.btn-secondary {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-primary:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #545b62;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-body {
    padding: 16px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn-primary,
  .btn-danger,
  .btn-secondary {
    width: 100%;
  }
}
</style> 