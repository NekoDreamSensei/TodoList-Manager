package com.todolist.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题实体类
 * 
 * 表示用户创建的主题分类，用于组织和分类任务
 * 每个主题属于一个用户，可以包含多个任务
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Entity
@Table(name = "topics")
public class Topic {
    
    /**
     * 主题唯一标识符
     * 主键，自增长，用于唯一标识每个主题
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 主题名称
     * 不能为空，用于显示和识别主题
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * 主题描述
     * 可选字段，用于详细描述主题的用途和内容
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 创建时间
     * 记录主题创建的时间戳
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 记录主题信息最后更新的时间戳
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 所属用户
     * 多对一关系，每个主题属于一个用户
     * 不能为空，级联操作
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 主题下的任务列表
     * 一对多关系，一个主题可以包含多个任务
     * 级联操作，懒加载
     */
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    
    /**
     * 默认构造函数
     * JPA要求无参构造函数
     */
    public Topic() {}
    
    /**
     * 带参数的构造函数
     * 创建新主题时使用，自动设置创建和更新时间
     * 
     * @param name 主题名称
     * @param description 主题描述
     * @param user 所属用户
     */
    public Topic(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== Getter和Setter方法 ==========
    
    /**
     * 获取主题ID
     * 
     * @return 主题ID
     */
    public Long getId() { return id; }
    
    /**
     * 设置主题ID
     * 
     * @param id 主题ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取主题名称
     * 
     * @return 主题名称
     */
    public String getName() { return name; }
    
    /**
     * 设置主题名称
     * 
     * @param name 主题名称
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * 获取主题描述
     * 
     * @return 主题描述
     */
    public String getDescription() { return description; }
    
    /**
     * 设置主题描述
     * 
     * @param description 主题描述
     */
    public void setDescription(String description) { this.description = description; }
    
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
     * 获取所属用户
     * 
     * @return 所属用户
     */
    public User getUser() { return user; }
    
    /**
     * 设置所属用户
     * 
     * @param user 所属用户
     */
    public void setUser(User user) { this.user = user; }
    
    /**
     * 获取主题下的任务列表
     * 
     * @return 任务列表
     */
    public List<Task> getTasks() { return tasks; }
    
    /**
     * 设置主题下的任务列表
     * 
     * @param tasks 任务列表
     */
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    
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
