<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>管理专题</h3>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="modal-body">
        <div class="form-group">
          <label for="topicName">专题名称 *</label>
          <input
            id="topicName"
            v-model="formData.name"
            type="text"
            required
            placeholder="请输入专题名称"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label for="topicDescription">专题描述</label>
          <textarea
            id="topicDescription"
            v-model="formData.description"
            placeholder="请输入专题描述（可选）"
            class="form-textarea"
            rows="3"
          ></textarea>
        </div>
        
        <div class="modal-actions">
          <button type="button" @click="handleDelete" class="btn-delete">删除专题</button>
          <button type="button" @click="closeModal" class="btn-cancel">取消</button>
          <button type="submit" class="btn-primary">保存修改</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

const props = defineProps({
  topic: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'update', 'delete'])

const formData = reactive({
  name: '',
  description: ''
})

onMounted(() => {
  formData.name = props.topic.name || ''
  formData.description = props.topic.description || ''
})

const handleSubmit = () => {
  if (!formData.name.trim()) {
    alert('请输入专题名称')
    return
  }
  
  emit('update', {
    ...props.topic,
    ...formData
  })
  closeModal()
}

const handleDelete = () => {
  if (confirm('确定要删除这个专题吗？这将删除专题下的所有任务和待办事项。')) {
    emit('delete', props.topic.id)
    closeModal()
  }
}

const closeModal = () => {
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
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #718096;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
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
  color: #2d3748;
  font-weight: 500;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 24px;
}

.btn-delete {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: #c53030;
}

.btn-cancel {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style>
