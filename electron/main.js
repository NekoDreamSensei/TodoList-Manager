import { app, BrowserWindow, ipcMain, dialog } from 'electron'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'
import { readFile, writeFile, access, mkdir } from 'fs/promises'
import { existsSync } from 'fs'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

// 数据存储目录
const DATA_DIR = join(app.getPath('userData'), 'data')
const USERS_FILE = join(DATA_DIR, 'users.json')
const TOPICS_FILE = join(DATA_DIR, 'topics.json')

// 确保数据目录存在
async function ensureDataDir() {
  try {
    await access(DATA_DIR)
  } catch {
    await mkdir(DATA_DIR, { recursive: true })
  }
}

// 创建主窗口
function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: join(__dirname, 'preload.js')
    },
    icon: join(__dirname, 'assets', 'icon.png'), // 可选：应用图标
    title: 'TodoList 管理系统'
  })

  // 开发环境加载本地服务器
  if (process.env.NODE_ENV === 'development') {
    mainWindow.loadURL('http://localhost:5173')
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(join(__dirname, '../dist/index.html'))
  }

  return mainWindow
}

// 应用准备就绪时创建窗口
app.whenReady().then(async () => {
  await ensureDataDir()
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 所有窗口关闭时退出应用
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// IPC 处理器：保存用户数据
ipcMain.handle('save-user-data', async (event, { username, topics }) => {
  try {
    await ensureDataDir()
    
    // 读取现有数据
    let allTopics = {}
    try {
      const data = await readFile(TOPICS_FILE, 'utf8')
      allTopics = JSON.parse(data)
    } catch {
      allTopics = {}
    }
    
    // 更新用户数据
    allTopics[username] = topics
    
    // 保存到文件
    await writeFile(TOPICS_FILE, JSON.stringify(allTopics, null, 2), 'utf8')
    
    // 同时保存用户备份文件
    const userBackupFile = join(DATA_DIR, `${username}_backup.json`)
    const backupData = {
      username,
      topics,
      backupTime: new Date().toISOString(),
      version: '1.0'
    }
    await writeFile(userBackupFile, JSON.stringify(backupData, null, 2), 'utf8')
    
    return { success: true, message: '数据保存成功' }
  } catch (error) {
    console.error('保存用户数据失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：加载用户数据
ipcMain.handle('load-user-data', async (event, { username }) => {
  try {
    await ensureDataDir()
    
    // 尝试从主数据文件加载
    try {
      const data = await readFile(TOPICS_FILE, 'utf8')
      const allTopics = JSON.parse(data)
      const userTopics = allTopics[username] || []
      return { success: true, topics: userTopics }
    } catch {
      // 如果主文件不存在，尝试从用户备份文件加载
      const userBackupFile = join(DATA_DIR, `${username}_backup.json`)
      try {
        const backupData = await readFile(userBackupFile, 'utf8')
        const parsed = JSON.parse(backupData)
        return { success: true, topics: parsed.topics || [] }
      } catch {
        return { success: true, topics: [] }
      }
    }
  } catch (error) {
    console.error('加载用户数据失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：保存用户信息
ipcMain.handle('save-user', async (event, { user }) => {
  try {
    await ensureDataDir()
    
    // 读取现有用户
    let users = []
    try {
      const data = await readFile(USERS_FILE, 'utf8')
      users = JSON.parse(data)
    } catch {
      users = []
    }
    
    // 检查用户是否已存在
    const existingUserIndex = users.findIndex(u => u.username === user.username)
    if (existingUserIndex !== -1) {
      users[existingUserIndex] = user
    } else {
      users.push(user)
    }
    
    // 保存用户列表
    await writeFile(USERS_FILE, JSON.stringify(users, null, 2), 'utf8')
    
    return { success: true, message: '用户信息保存成功' }
  } catch (error) {
    console.error('保存用户信息失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：加载用户信息
ipcMain.handle('load-user', async (event, { username }) => {
  try {
    await ensureDataDir()
    
    try {
      const data = await readFile(USERS_FILE, 'utf8')
      const users = JSON.parse(data)
      const user = users.find(u => u.username === username)
      return { success: true, user }
    } catch {
      return { success: true, user: null }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：导出数据到指定位置
ipcMain.handle('export-data', async (event, { username, data, format }) => {
  try {
    const result = await dialog.showSaveDialog({
      title: '导出数据',
      defaultPath: `${username}_todolist.${format === 'markdown' ? 'md' : 'json'}`,
      filters: [
        { name: format === 'markdown' ? 'Markdown文件' : 'JSON文件', extensions: [format === 'markdown' ? 'md' : 'json'] }
      ]
    })
    
    if (!result.canceled && result.filePath) {
      const content = format === 'markdown' ? data : JSON.stringify(data, null, 2)
      await writeFile(result.filePath, content, 'utf8')
      return { success: true, message: '数据导出成功', filePath: result.filePath }
    } else {
      return { success: false, message: '导出已取消' }
    }
  } catch (error) {
    console.error('导出数据失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：导入数据从指定位置
ipcMain.handle('import-data', async (event) => {
  try {
    const result = await dialog.showOpenDialog({
      title: '导入数据',
      properties: ['openFile'],
      filters: [
        { name: '所有支持的文件', extensions: ['json', 'md', 'txt'] },
        { name: 'JSON文件', extensions: ['json'] },
        { name: 'Markdown文件', extensions: ['md', 'txt'] }
      ]
    })
    
    if (!result.canceled && result.filePaths.length > 0) {
      const filePath = result.filePaths[0]
      const content = await readFile(filePath, 'utf8')
      const fileName = filePath.split(/[/\\]/).pop()
      
      return { 
        success: true, 
        content, 
        fileName,
        format: fileName.endsWith('.json') ? 'json' : 'markdown'
      }
    } else {
      return { success: false, message: '导入已取消' }
    }
  } catch (error) {
    console.error('导入数据失败:', error)
    return { success: false, error: error.message }
  }
})

// IPC 处理器：获取数据目录信息
ipcMain.handle('get-data-info', async () => {
  try {
    await ensureDataDir()
    
    const info = {
      dataDir: DATA_DIR,
      usersFile: USERS_FILE,
      topicsFile: TOPICS_FILE,
      exists: {
        dataDir: existsSync(DATA_DIR),
        usersFile: existsSync(USERS_FILE),
        topicsFile: existsSync(TOPICS_FILE)
      }
    }
    
    return { success: true, info }
  } catch (error) {
    console.error('获取数据目录信息失败:', error)
    return { success: false, error: error.message }
  }
}) 