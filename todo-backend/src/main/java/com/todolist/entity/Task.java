package com.todolist.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务实体类
 * 
 * 表示待办事项中的任务，每个任务属于一个主题
 * 任务可以包含多个待办事项，用于细化和跟踪任务进度
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Entity
@Table(name = "tasks")
public class Task {
    
    /**
     * 任务唯一标识符
     * 主键，自增长，用于唯一标识每个任务
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 任务标题
     * 不能为空，用于显示和识别任务
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * 任务描述
     * 可选字段，用于详细描述任务的内容和要求
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 任务状态
     * 表示任务的当前状态，如：待开始、进行中、已完成等
     */
    @Column(name = "status")
    private String status;
    
    /**
     * 任务优先级
     * 表示任务的重要程度，如：高、中、低
     */
    @Column(name = "priority")
    private String priority;
    
    /**
     * 截止时间
     * 任务的预期完成时间，可选字段
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    /**
     * 创建时间
     * 记录任务创建的时间戳
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 记录任务信息最后更新的时间戳
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 所属主题
     * 多对一关系，每个任务属于一个主题
     * 不能为空，级联操作
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    /**
     * 任务下的待办事项列表
     * 一对多关系，一个任务可以包含多个待办事项
     * 级联操作，懒加载
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Todo> todos = new ArrayList<>();
    
    /**
     * 默认构造函数
     * JPA要求无参构造函数
     */
    public Task() {}
    
    /**
     * 带参数的构造函数
     * 创建新任务时使用，自动设置创建和更新时间
     * 
     * @param title 任务标题
     * @param description 任务描述
     * @param topic 所属主题
     */
    public Task(String title, String description, Topic topic) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.status = "待开始";
        this.priority = "中";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== Getter和Setter方法 ==========
    
    /**
     * 获取任务ID
     * 
     * @return 任务ID
     */
    public Long getId() { return id; }
    
    /**
     * 设置任务ID
     * 
     * @param id 任务ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取任务标题
     * 
     * @return 任务标题
     */
    public String getTitle() { return title; }
    
    /**
     * 设置任务标题
     * 
     * @param title 任务标题
     */
    public void setTitle(String title) { this.title = title; }
    
    /**
     * 获取任务描述
     * 
     * @return 任务描述
     */
    public String getDescription() { return description; }
    
    /**
     * 设置任务描述
     * 
     * @param description 任务描述
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * 获取任务状态
     * 
     * @return 任务状态
     */
    public String getStatus() { return status; }
    
    /**
     * 设置任务状态
     * 
     * @param status 任务状态
     */
    public void setStatus(String status) { this.status = status; }
    
    /**
     * 获取任务优先级
     * 
     * @return 任务优先级
     */
    public String getPriority() { return priority; }
    
    /**
     * 设置任务优先级
     * 
     * @param priority 任务优先级
     */
    public void setPriority(String priority) { this.priority = priority; }
    
    /**
     * 获取截止时间
     * 
     * @return 截止时间
     */
    public LocalDateTime getDueDate() { return dueDate; }
    
    /**
     * 设置截止时间
     * 
     * @param dueDate 截止时间
     */
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    /**
     * 获取创建时间
     * 
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * 设置创建时间
     * 
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * 获取更新时间
     * 
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    /**
     * 设置更新时间
     * 
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * 获取所属主题
     * 
     * @return 所属主题
     */
    public Topic getTopic() { return topic; }
    
    /**
     * 设置所属主题
     * 
     * @param topic 所属主题
     */
    public void setTopic(Topic topic) { this.topic = topic; }
    
    /**
     * 获取任务下的待办事项列表
     * 
     * @return 待办事项列表
     */
    public List<Todo> getTodos() { return todos; }
    
    /**
     * 设置任务下的待办事项列表
     * 
     * @param todos 待办事项列表
     */
    public void setTodos(List<Todo> todos) { this.todos = todos; }
    
    /**
     * 更新前的回调方法
     * JPA生命周期回调，在实体更新前自动调用
     * 自动更新updatedAt字段为当前时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}