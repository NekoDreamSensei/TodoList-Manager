package com.todolist.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TopicDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private List<TaskDTO> tasks;

    // 构造函数
    public TopicDTO() {}

    public TopicDTO(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<TaskDTO> getTasks() { return tasks; }
    public void setTasks(List<TaskDTO> tasks) { this.tasks = tasks; }
}
