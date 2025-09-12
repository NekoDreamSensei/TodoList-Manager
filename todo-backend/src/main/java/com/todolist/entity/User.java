package com.todolist.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 * 
 * 表示系统中的用户信息，包含用户的基本信息和关联的主题列表
 * 使用JPA注解进行数据库映射，支持用户认证和权限管理
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Entity
@Table(name = "users")
public class User {
    
    /**
     * 用户唯一标识符
     * 主键，自增长，用于唯一标识每个用户
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     * 唯一标识，不能为空，用于用户登录和显示
     */
    @Column(unique = true, nullable = false)
    private String username;
    
    /**
     * 用户密码
     * 不能为空，用于用户身份验证
     * 注意：实际项目中应该加密存储
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 创建时间
     * 记录用户账户创建的时间戳
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 记录用户信息最后更新的时间戳
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 用户关联的主题列表
     * 一对多关系，一个用户可以有多个主题
     * 级联操作，懒加载，提高性能
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();
    
    /**
     * 默认构造函数
     * JPA要求无参构造函数
     */
    public User() {}
    
    /**
     * 带参数的构造函数
     * 创建新用户时使用，自动设置创建和更新时间
     * 
     * @param username 用户名
     * @param password 密码
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== Getter和Setter方法 ==========
    
    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getId() { return id; }
    
    /**
     * 设置用户ID
     * 
     * @param id 用户ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() { return username; }
    
    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public void setUsername(String username) { this.username = username; }
    
    /**
     * 获取用户密码
     * 
     * @return 用户密码
     */
    public String getPassword() { return password; }
    
    /**
     * 设置用户密码
     * 
     * @param password 用户密码
     */
    public void setPassword(String password) { this.password = password; }
    
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
     * 获取用户关联的主题列表
     * 
     * @return 主题列表
     */
    public List<Topic> getTopics() { return topics; }
    
    /**
     * 设置用户关联的主题列表
     * 
     * @param topics 主题列表
     */
    public void setTopics(List<Topic> topics) { this.topics = topics; }
    
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