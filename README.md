# TodoList 管理系统

一个基于 Vue 3 + Spring Boot 的前后端分离个人待办事项管理系统，支持多层级任务管理和进度跟踪。

## 功能特性

### 🚀 核心功能
- **用户管理**: 支持用户注册和登录，JWT 身份验证
- **专题管理**: 创建和管理不同的工作/学习专题
- **任务管理**: 在每个专题下创建具体的任务
- **待办清单**: 为每个任务创建详细的待办事项
- **进度跟踪**: 实时显示各层级的完成进度
- **仪表盘**: 多视图模式展示（卡片视图、层级视图、图表视图）

### 📊 进度展示
- **专题层**: 饼状图显示各专题完成情况
- **任务层**: 柱状图显示各任务完成进度  
- **清单层**: 进度条 + 备注系统，支持拖拽调整进度
- **热力图**: 活动热力图展示使用情况

### 💾 数据存储
- **后端数据库**: Spring Boot + JPA + H2 内存数据库
- **RESTful API**: 完整的 REST API 接口
- **JWT 认证**: 安全的用户身份验证
- **数据持久化**: 数据库存储，支持数据备份和恢复

## 技术架构

### 前端技术栈
- **框架**: Vue 3 + Composition API
- **构建工具**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP 客户端**: Axios
- **样式**: CSS3 + 响应式设计
- **桌面应用**: Electron

### 后端技术栈
- **框架**: Spring Boot 3.5.5
- **数据库**: H2 内存数据库
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT
- **API 文档**: OpenAPI 3 (Swagger)
- **构建工具**: Maven
- **Java 版本**: Java 24

## 项目结构

```
newtodolist/
├── src/                          # 前端源码
│   ├── components/               # Vue 组件
│   │   ├── AuthForm.vue         # 用户认证组件
│   │   ├── CreateTopicModal.vue # 创建专题模态框
│   │   ├── CreateTaskModal.vue  # 创建任务模态框
│   │   ├── CreateTodoModal.vue  # 创建待办模态框
│   │   ├── EditTaskModal.vue    # 编辑任务模态框
│   │   ├── EditTodoModal.vue    # 编辑待办模态框
│   │   ├── EditTopicModal.vue   # 编辑专题模态框
│   │   └── ProgressOverview.vue # 进度概览组件
│   ├── views/                   # 页面组件
│   │   ├── DashboardPage.vue    # 仪表盘页面
│   │   ├── LoginPage.vue        # 登录页面
│   │   ├── MainLayout.vue       # 主布局
│   │   ├── TestPage.vue         # 测试页面
│   │   └── TopicDetailPage.vue  # 专题详情页面
│   ├── services/                # API 服务
│   │   ├── api.js              # API 配置
│   │   ├── authService.js      # 认证服务
│   │   ├── taskService.js      # 任务服务
│   │   ├── todoService.js      # 待办服务
│   │   ├── topicService.js     # 专题服务
│   │   └── userService.js      # 用户服务
│   ├── stores/                  # 状态管理
│   │   └── useTodoStore.js     # 待办状态管理
│   ├── router/                  # 路由配置
│   │   └── index.js            # 路由定义
│   ├── utils/                   # 工具函数
│   │   └── markdownParser.js   # Markdown 解析器
│   ├── assets/                  # 静态资源
│   └── main.js                  # 应用入口
├── todo-backend/                # 后端源码
│   ├── src/main/java/com/todolist/
│   │   ├── controller/          # REST 控制器
│   │   ├── service/            # 业务逻辑层
│   │   ├── repository/         # 数据访问层
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── filter/             # 过滤器
│   │   ├── exception/          # 异常处理
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       └── application.yaml    # 应用配置
├── electron/                    # Electron 配置
│   ├── main.js                 # 主进程
│   └── preload.js              # 预加载脚本
└── public/                      # 公共资源
```

## 快速开始

### 环境要求
- **前端**: Node.js >= 20.19.0 或 >= 22.12.0
- **后端**: Java 24, Maven 3.6+
- **数据库**: H2 (内嵌，无需额外安装)

### 1. 克隆项目
```bash
git clone <repository-url>
cd newtodolist
```

### 2. 启动后端服务
```bash
cd todo-backend
./mvnw spring-boot:run
```
后端服务将在 `http://localhost:8080` 启动

### 3. 启动前端服务
```bash
# 在项目根目录
npm install
npm run dev
```
前端服务将在 `http://localhost:5173` 启动

### 4. 启动桌面应用（可选）
```bash
npm run electron:dev
```

## API 文档

启动后端服务后，访问以下地址查看 API 文档：
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 主要 API 端点

#### 用户认证
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

#### 专题管理
- `GET /api/topics` - 获取用户专题列表
- `POST /api/topics` - 创建专题
- `PUT /api/topics/{id}` - 更新专题
- `DELETE /api/topics/{id}` - 删除专题

#### 任务管理
- `GET /api/topics/{topicId}/tasks` - 获取专题下的任务
- `POST /api/topics/{topicId}/tasks` - 创建任务
- `PUT /api/tasks/{id}` - 更新任务
- `DELETE /api/tasks/{id}` - 删除任务

#### 待办管理
- `GET /api/tasks/{taskId}/todos` - 获取任务下的待办
- `POST /api/tasks/{taskId}/todos` - 创建待办
- `PUT /api/todos/{id}` - 更新待办
- `DELETE /api/todos/{id}` - 删除待办

## 使用说明

### 1. 用户注册/登录
- 首次使用需要注册账号
- 支持用户名和密码登录
- 使用 JWT Token 进行身份验证

### 2. 创建专题
- 点击"新建专题"按钮
- 输入专题名称和描述
- 专题将显示在左侧边栏

### 3. 创建任务
- 选择专题后，点击"新建任务"
- 为任务添加名称和描述
- 任务将显示在专题详情页面

### 4. 管理待办事项
- 选择任务后，点击"新建待办"
- 设置待办标题、描述、初始进度和备注
- 使用进度条调整完成进度
- 支持编辑和删除待办事项

### 5. 仪表盘视图
- **卡片视图**: 以卡片形式展示专题和任务
- **层级视图**: 树形结构展示层级关系
- **图表视图**: 图表和统计信息展示

## 数据模型

### 用户 (User)
```java
@Entity
public class User {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
```

### 专题 (Topic)
```java
@Entity
public class Topic {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private User user;
    private List<Task> tasks;
}
```

### 任务 (Task)
```java
@Entity
public class Task {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Topic topic;
    private List<Todo> todos;
}
```

### 待办 (Todo)
```java
@Entity
public class Todo {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Integer progress;
    private String note;
    private LocalDateTime createdAt;
    private Task task;
}
```

## 开发指南

### 前端开发
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

### 后端开发
```bash
# 进入后端目录
cd todo-backend

# 运行测试
./mvnw test

# 启动开发服务器
./mvnw spring-boot:run

# 打包应用
./mvnw package
```

### 桌面应用开发
```bash
# 开发模式
npm run electron:dev

# 构建桌面应用
npm run electron:build
```

## 配置说明

### 前端配置
- `vite.config.js` - Vite 构建配置
- `jsconfig.json` - JavaScript 项目配置

### 后端配置
- `application.yaml` - Spring Boot 应用配置
- `pom.xml` - Maven 依赖配置

## 浏览器兼容性

- Chrome >= 88
- Firefox >= 85
- Safari >= 14
- Edge >= 88

## 开发计划

### 近期功能
- [ ] 数据导出/导入功能
- [ ] 任务优先级设置
- [ ] 截止日期提醒
- [ ] 标签分类系统
- [ ] 数据统计分析

### 长期规划
- [ ] 多用户协作
- [ ] 云端数据同步
- [ ] 移动端适配
- [ ] 微服务架构
- [ ] Docker 容器化

## 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发规范
- **前端**: 使用 Vue 3 Composition API，遵循 ESLint 规则
- **后端**: 遵循 Spring Boot 最佳实践，使用 JPA 规范
- **组件命名**: 使用 PascalCase
- **文件命名**: 使用 kebab-case
- **API 设计**: 遵循 RESTful 规范

## 许可证

MIT License

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 GitHub Issue
- 发送邮件至项目维护者

---

**注意**: 这是一个前后端分离的应用，前端使用 Vue 3，后端使用 Spring Boot。数据存储在 H2 内存数据库中，重启服务后数据会重置。生产环境建议使用 MySQL 或 PostgreSQL 等持久化数据库。