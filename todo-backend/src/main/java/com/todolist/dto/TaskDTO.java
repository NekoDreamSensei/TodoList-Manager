package com.todolist.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long topicId;
    private List<TodoDTO> todos;

    // 构造函数
    public TaskDTO() {}

    public TaskDTO(Long id, String title, String description, String status, String priority, LocalDateTime dueDate, LocalDateTime createdAt, LocalDateTime updatedAt, Long topicId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.topicId = topicId;
    }

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public List<TodoDTO> getTodos() { return todos; }
    public void setTodos(List<TodoDTO> todos) { this.todos = todos; }
}
