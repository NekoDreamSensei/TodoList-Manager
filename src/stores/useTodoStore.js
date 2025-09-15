// src/stores/useTodoStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTodoStore = defineStore('todo', () => {
  // 状态
  const topics = ref([])
  const selectedTopic = ref(null)
  const selectedTask = ref(null)

  // 加载用户专题数据
  const loadUserTopics = (username) => {
    if (!username) return
    
    const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
    const userTopics = allTopics[username] || []
    topics.value = userTopics
    
    console.log(`加载用户 ${username} 的数据:`, {
      topicsCount: userTopics.length,
      totalTasks: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.length : 0), 0),
      totalTodos: userTopics.reduce((sum, t) => sum + (t.tasks ? t.tasks.reduce((s, task) => s + (task.todos ? task.todos.length : 0), 0) : 0), 0)
    })
  }

  // 保存数据
  const saveData = (username) => {
    if (!username) return
    
    const allTopics = JSON.parse(localStorage.getItem('topics') || '{}')
    allTopics[username] = topics.value
    localStorage.setItem('topics', JSON.stringify(allTopics))
    
    console.log(`保存用户 ${username} 的数据:`, {
      topicsCount: topics.value.length
    })
  }

  // 清除数据
  const clearData = () => {
    topics.value = []
    selectedTopic.value = null
    selectedTask.value = null
  }

  return {
    topics,
    selectedTopic,
    selectedTask,
    loadUserTopics,
    saveData,
    clearData
  }
})