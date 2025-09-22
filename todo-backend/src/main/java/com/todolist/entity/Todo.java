package com.todolist.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Todo {
    
    /**
     * 待办事项唯一标识符
     * 主键，自增长，用于唯一标识每个待办事项
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 待办事项标题
     * 不能为空，用于显示和识别待办事项
     */
    @Column(name = "title", nullable = false)
    private String title;
    
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
     * 完成状态（兼容旧字段）
     * 与 isCompleted 字段相同，用于向后兼容
     */
    @Column(name = "completed")
    private Boolean completed = false;
    
    /**
     * 进度百分比
     * 表示待办事项的完成进度，0-100
     */
    @Column(name = "progress")
    private Integer progress = 0;
    
    /**
     * 备注
     * 待办事项的额外说明
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
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
    @JsonBackReference
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
     * @param title 待办事项标题
     * @param content 待办事项内容
     * @param task 所属任务
     */
    public Todo(String title, String content, Task task) {
        this.title = title;
        this.content = content;
        this.task = task;
        this.isCompleted = false;
        this.completed = false;
        this.progress = 0;
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
     * 获取待办事项标题
     * 
     * @return 待办事项标题
     */
    public String getTitle() { return title; }
    
    /**
     * 设置待办事项标题
     * 
     * @param title 待办事项标题
     */
    public void setTitle(String title) { this.title = title; }
    
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
        this.completed = isCompleted; // 同步设置 completed 字段
    }
    
    /**
     * 获取完成状态（兼容性方法）
     * 
     * @return 完成状态
     */
    public Boolean getCompleted() { return completed; }
    
    /**
     * 设置完成状态（兼容性方法）
     * 
     * @param completed 完成状态
     */
    public void setCompleted(Boolean completed) { 
        this.completed = completed;
        this.isCompleted = completed; // 同步设置 isCompleted 字段
    }
    
    /**
     * 获取进度百分比
     * 
     * @return 进度百分比
     */
    public Integer getProgress() { return progress; }
    
    /**
     * 设置进度百分比
     * 
     * @param progress 进度百分比
     */
    public void setProgress(Integer progress) { 
        this.progress = progress;
        // 如果进度是100%，自动标记为完成
        if (progress != null && progress >= 100) {
            this.isCompleted = true;
            this.completed = true;
        }
    }
    
    /**
     * 获取备注
     * 
     * @return 备注
     */
    public String getNote() { return note; }
    
    /**
     * 设置备注
     * 
     * @param note 备注
     */
    public void setNote(String note) { this.note = note; }
    
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
     * 创建前的回调方法
     * JPA生命周期回调，在实体保存前自动调用
     * 自动设置创建和更新时间
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // 确保 completed 和 isCompleted 字段同步
        if (this.completed == null && this.isCompleted != null) {
            this.completed = this.isCompleted;
        }
        if (this.isCompleted == null && this.completed != null) {
            this.isCompleted = this.completed;
        }
        if (this.isCompleted == null && this.completed == null) {
            this.isCompleted = false;
            this.completed = false;
        }
        // 确保进度字段不为空
        if (this.progress == null) {
            this.progress = 0;
        }
    }
    
    /**
     * 更新前的回调方法
     * JPA生命周期回调，在实体更新前自动调用
     * 自动更新updatedAt字段为当前时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        // 确保 completed 和 isCompleted 字段同步
        if (this.completed == null && this.isCompleted != null) {
            this.completed = this.isCompleted;
        }
        if (this.isCompleted == null && this.completed != null) {
            this.isCompleted = this.completed;
        }
    }
}