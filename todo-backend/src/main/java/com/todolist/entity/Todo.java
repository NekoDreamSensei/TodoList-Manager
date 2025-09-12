package com.todolist.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 待办事项实体类
 * 
 * 表示任务的具体执行步骤，每个待办事项属于一个任务
 * 用于细化和跟踪任务的具体执行过程
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Entity
@Table(name = "todos")
public class Todo {
    
    /**
     * 待办事项唯一标识符
     * 主键，自增长，用于唯一标识每个待办事项
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 待办事项内容
     * 不能为空，描述需要完成的具体事项
     */
    @Column(nullable = false)
    private String content;
    
    /**
     * 完成状态
     * 表示待办事项是否已完成，默认为false
     */
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    /**
     * 完成时间
     * 记录待办事项完成的时间戳，未完成时为null
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    /**
     * 创建时间
     * 记录待办事项创建的时间戳
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 记录待办事项信息最后更新的时间戳
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 所属任务
     * 多对一关系，每个待办事项属于一个任务
     * 不能为空，级联操作
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    /**
     * 默认构造函数
     * JPA要求无参构造函数
     */
    public Todo() {}
    
    /**
     * 带参数的构造函数
     * 创建新待办事项时使用，自动设置创建和更新时间
     * 
     * @param content 待办事项内容
     * @param task 所属任务
     */
    public Todo(String content, Task task) {
        this.content = content;
        this.task = task;
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== Getter和Setter方法 ==========
    
    /**
     * 获取待办事项ID
     * 
     * @return 待办事项ID
     */
    public Long getId() { return id; }
    
    /**
     * 设置待办事项ID
     * 
     * @param id 待办事项ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取待办事项内容
     * 
     * @return 待办事项内容
     */
    public String getContent() { return content; }
    
    /**
     * 设置待办事项内容
     * 
     * @param content 待办事项内容
     */
    public void setContent(String content) { this.content = content; }
    
    /**
     * 获取完成状态
     * 
     * @return 完成状态
     */
    public Boolean getIsCompleted() { return isCompleted; }
    
    /**
     * 设置完成状态
     * 
     * @param isCompleted 完成状态
     */
    public void setIsCompleted(Boolean isCompleted) { 
        this.isCompleted = isCompleted;
        if (isCompleted && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (!isCompleted) {
            this.completedAt = null;
        }
    }
    
    /**
     * 获取完成时间
     * 
     * @return 完成时间
     */
    public LocalDateTime getCompletedAt() { return completedAt; }
    
    /**
     * 设置完成时间
     * 
     * @param completedAt 完成时间
     */
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
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
     * 获取所属任务
     * 
     * @return 所属任务
     */
    public Task getTask() { return task; }
    
    /**
     * 设置所属任务
     * 
     * @param task 所属任务
     */
    public void setTask(Task task) { this.task = task; }
    
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