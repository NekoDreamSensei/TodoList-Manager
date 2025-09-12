import { contextBridge, ipcRenderer } from 'electron'

// 暴露安全的API给渲染进程
contextBridge.exposeInMainWorld('electronAPI', {
  // 数据存储相关
  saveUserData: (username, topics) => ipcRenderer.invoke('save-user-data', { username, topics }),
  loadUserData: (username) => ipcRenderer.invoke('load-user-data', { username }),
  saveUser: (user) => ipcRenderer.invoke('save-user', { user }),
  loadUser: (username) => ipcRenderer.invoke('load-user', { username }),
  
  // 导入导出相关
  exportData: (username, data, format) => ipcRenderer.invoke('export-data', { username, data, format }),
  importData: () => ipcRenderer.invoke('import-data'),
  
  // 系统信息
  getDataInfo: () => ipcRenderer.invoke('get-data-info'),
  
  // 检查是否在Electron环境中
  isElectron: true
}) 