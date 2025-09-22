package com.todolist.dto;

import java.time.LocalDateTime;

public class TodoDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean isCompleted;
    private Boolean completed;
    private Integer progress;
    private String note;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long taskId;

    // 构造函数
    public TodoDTO() {}

    public TodoDTO(Long id, String title, String content, Boolean isCompleted, Boolean completed, 
                   Integer progress, String note, LocalDateTime completedAt, 
                   LocalDateTime createdAt, LocalDateTime updatedAt, Long taskId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isCompleted = isCompleted;
        this.completed = completed;
        this.progress = progress;
        this.note = note;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.taskId = taskId;
    }

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
}
