// electron/preload.js
const { contextBridge, ipcRenderer } = require('electron')

contextBridge.exposeInMainWorld('electronAPI', {
  // 可以在这里暴露一些安全的API给渲染进程
  platform: process.platform,
  versions: process.versions
})