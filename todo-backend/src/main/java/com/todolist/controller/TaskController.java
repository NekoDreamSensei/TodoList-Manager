package com.todolist.controller;

import com.todolist.entity.Task;
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
@CrossOrigin(origins = "*")
@Tag(name = "任务管理", description = "任务相关的API操作")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 根据主题ID获取该主题下的所有任务
     * 
     * @param topicId 主题ID
     * @return List<Task> 该主题下的任务列表
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "获取主题下的所有任务", description = "根据主题ID获取该主题下的所有任务列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取任务列表"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<Task> getTasksByTopicId(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long topicId) {
        return taskService.getTasksByTopicId(topicId);
    }
    
    /**
     * 根据ID获取特定任务
     * 
     * @param id 任务ID
     * @return ResponseEntity<Task> 包含任务信息的响应实体，如果任务不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取任务", description = "根据任务ID获取特定任务的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取任务信息"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 在指定主题下创建新任务
     * 
     * @param topicId 主题ID
     * @param task 要创建的任务信息
     * @return Task 创建成功的任务信息（包含生成的ID）
     */
    @PostMapping("/topic/{topicId}")
    @Operation(summary = "创建新任务", description = "在指定主题下创建新的任务")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建任务"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Task createTask(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long topicId, 
            @Parameter(description = "任务信息", required = true)
            @RequestBody Task task) {
        return taskService.createTask(task, topicId);
    }
    
    /**
     * 更新任务信息
     * 
     * @param id 要更新的任务ID
     * @param task 新的任务信息
     * @return ResponseEntity<Task> 更新后的任务信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新任务信息", description = "根据任务ID更新任务信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新任务信息"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "新的任务信息", required = true)
            @RequestBody Task task) {
        task.setId(id);
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
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
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}