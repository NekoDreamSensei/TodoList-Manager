package com.todolist.controller;

import com.todolist.dto.TaskDTO;
import com.todolist.dto.TodoDTO;
import com.todolist.entity.Task;
import com.todolist.entity.Todo;
import com.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务控制器
 * 
 * 负责处理与任务相关的HTTP请求，包括任务的增删改查操作
 * 任务是待办事项的基本单位，每个任务属于一个主题
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "任务管理", description = "任务相关的API操作")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 根据主题ID获取该主题下的所有任务
     * 
     * @param topicId 主题ID
     * @return List<TaskDTO> 该主题下的任务列表
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "获取主题下的所有任务", description = "根据主题ID获取该主题下的所有任务列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取任务列表"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<TaskDTO> getTasksByTopicId(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long topicId) {
        List<Task> tasks = taskService.getTasksByTopicId(topicId);
        return tasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取特定任务
     * 
     * @param id 任务ID
     * @return ResponseEntity<TaskDTO> 包含任务信息的响应实体，如果任务不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取任务", description = "根据任务ID获取特定任务的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取任务信息"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TaskDTO> getTaskById(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(t -> ResponseEntity.ok(convertToDTO(t)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 在指定主题下创建新任务
     * 
     * @param topicId 主题ID
     * @param taskDTO 要创建的任务信息
     * @return TaskDTO 创建成功的任务信息（包含生成的ID）
     */
    @PostMapping("/topic/{topicId}")
    @Operation(summary = "创建新任务", description = "在指定主题下创建新的任务")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建任务"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public TaskDTO createTask(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long topicId, 
            @Parameter(description = "任务信息", required = true)
            @RequestBody TaskDTO taskDTO) {
        Task task = convertToEntity(taskDTO);
        Task savedTask = taskService.createTask(task, topicId);
        return convertToDTO(savedTask);
    }
    
    /**
     * 更新任务信息
     * 
     * @param id 要更新的任务ID
     * @param taskDTO 新的任务信息
     * @return ResponseEntity<TaskDTO> 更新后的任务信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新任务信息", description = "根据任务ID更新任务信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新任务信息"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "新的任务信息", required = true)
            @RequestBody TaskDTO taskDTO) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setStatus(taskDTO.getStatus());
            task.setPriority(taskDTO.getPriority());
            task.setDueDate(taskDTO.getDueDate());
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(convertToDTO(updatedTask));
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 删除任务
     * 
     * @param id 要删除的任务ID
     * @return ResponseEntity<Void> 删除成功返回204状态码
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "根据任务ID删除任务及其所有相关待办事项")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功删除任务"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 将 Task 实体转换为 TaskDTO
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        // 安全地获取主题ID
        if (task.getTopic() != null) {
            dto.setTopicId(task.getTopic().getId());
        }
        
        // 如果需要包含待办事项列表，可以在这里添加
        // 但建议在需要时才加载，避免性能问题
        // if (task.getTodos() != null) {
        //     List<TodoDTO> todoDTOs = task.getTodos().stream()
        //         .map(this::convertTodoToDTO)
        //         .collect(Collectors.toList());
        //     dto.setTodos(todoDTOs);
        // }
        
        return dto;
    }
    
    /**
     * 将 TaskDTO 转换为 Task 实体
     */
    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        // 注意：不设置 topic，让 service 层处理
        return task;
    }
    
    /**
     * 将 Todo 实体转换为 TodoDTO（如果需要）
     */
    private TodoDTO convertTodoToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setContent(todo.getContent());
        dto.setIsCompleted(todo.getIsCompleted());
        dto.setCompletedAt(todo.getCompletedAt());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        
        if (todo.getTask() != null) {
            dto.setTaskId(todo.getTask().getId());
        }
        
        return dto;
    }
}